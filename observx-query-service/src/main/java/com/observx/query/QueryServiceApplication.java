package com.observx.query;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * ObservX Query Service - API for querying observability data.
 */
@SpringBootApplication(scanBasePackages = "com.observx")
@EnableJpaRepositories(basePackages = "com.observx.storage.repository")
@EntityScan(basePackages = "com.observx.storage.entity")
public class QueryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(QueryServiceApplication.class, args);
    }
}
