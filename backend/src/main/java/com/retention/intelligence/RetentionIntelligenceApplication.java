package com.retention.intelligence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.camunda.zeebe.spring.client.annotation.Deployment;

@SpringBootApplication
@Deployment(resources = "classpath:bpmn/CustomerRecoveryProcess.bpmn")
public class RetentionIntelligenceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RetentionIntelligenceApplication.class, args);
    }
}
