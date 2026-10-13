package com.retention.intelligence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RetentionIntelligenceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RetentionIntelligenceApplication.class, args);
    }
}
