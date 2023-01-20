package com.zjh.ecom.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.topics.customer}")
    private String customerTopic;
    @Value("${spring.kafka.topics.employee}")
    private String employeeTopic;
}
