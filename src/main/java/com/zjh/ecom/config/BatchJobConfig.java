package com.zjh.ecom.config;

import com.zjh.ecom.dao.CustomerRepository;
import com.zjh.ecom.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileUrlResource;

import java.net.MalformedURLException;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
@Slf4j
public class BatchJobConfig {

    private JobBuilderFactory jobBuilderFactory;

    private StepBuilderFactory stepBuilderFactory;

    private CustomerRepository customerRepository;

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
    public RepositoryItemWriter<Customer> itemWriter() {
        RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step loadCustomerStep() {
        return stepBuilderFactory.get("load-csv-step").<Customer, Customer>chunk(10)
                .reader(itemReader())
                .processor(customerProcessor())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job loadCustomerJob() {
        return jobBuilderFactory.get("load-csv-job").flow(loadCustomerStep())
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
}
