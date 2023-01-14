package com.zjh.ecom.config;

import com.zjh.ecom.dao.CustomerRepository;
import com.zjh.ecom.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.kafka.KafkaItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileUrlResource;
import org.springframework.kafka.core.KafkaTemplate;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@Slf4j
public class BatchJobConfig {

    @NotNull
    private JobBuilderFactory jobBuilderFactory;

    @NotNull
    private StepBuilderFactory stepBuilderFactory;

    @NotNull
    private CustomerRepository customerRepository;

    @NotNull
    private KafkaTemplate kafkaTemplate;

    @Value("${spring.kafka.topics.customer}")
    private String customerTopic;

    @Bean
    @SneakyThrows
    public FlatFileItemReader<Customer> itemReader() {
        FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();
        reader.setResource(new FileUrlResource("src/main/resources/customer.csv"));
        reader.setName("customerReader");
        reader.setLinesToSkip(1);
        reader.setLineMapper(lineMapper());

        return reader;
    }

    @Bean
    public CustomerProcessor customerProcessor() {
        return new CustomerProcessor();
    }

    @Bean
    public RepositoryItemWriter<Customer> repositoryItemWriter() {
        RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public KafkaItemWriter<Integer, Customer> kafkaItemWriter() {
        KafkaItemWriter<Integer, Customer> writer = new KafkaItemWriter<>();
        kafkaTemplate.setDefaultTopic(customerTopic);
        writer.setKafkaTemplate(kafkaTemplate);
        writer.setItemKeyMapper(Customer::getId);
        writer.setDelete(false);
        return writer;
    }

    @Bean
    public CompositeWriter<Customer> compositeWriter() {
        return new CompositeWriter<>(repositoryItemWriter(), kafkaItemWriter());
    }

    @Bean
    public Step loadCustomerFromCSV() {
        return stepBuilderFactory.get("load-csv-step").<Customer, Customer>chunk(10)
                .reader(itemReader())
                .processor(customerProcessor())
                .writer(repositoryItemWriter())
                .build();
    }

    @Bean
    public Job loadCustomerJob() {
        return jobBuilderFactory.get("load-csv-job")
                .flow(loadCustomerFromCSV())
                .end().build();
    }

    private LineMapper<Customer> lineMapper() {
        DefaultLineMapper<Customer> defaultLineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setStrict(false);
        tokenizer.setNames("firstName","lastName","email","gender","phoneNumber");

        BeanWrapperFieldSetMapper<Customer> mapper = new BeanWrapperFieldSetMapper();
        mapper.setTargetType(Customer.class);

        defaultLineMapper.setLineTokenizer(tokenizer);
        defaultLineMapper.setFieldSetMapper(mapper);
        return defaultLineMapper;
    }

    @AllArgsConstructor
    class CompositeWriter<Customer> implements ItemWriter<Customer> {

        private RepositoryItemWriter repositoryItemWriter;
        private KafkaItemWriter kafkaItemWriter;
        @Override
        public void write(List<? extends Customer> items) throws Exception {
            repositoryItemWriter.write(items);
            kafkaItemWriter.write(items);
        }
    }
}
