package com.ibm.hu.learningspringbatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class LearningSpringBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningSpringBatchApplication.class, args);
    }

}
