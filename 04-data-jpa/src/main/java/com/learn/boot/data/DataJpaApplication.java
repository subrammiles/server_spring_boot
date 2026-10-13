package com.learn.boot.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Compare with server_spring lessons 17-18: no DataSource, EntityManagerFactory, TransactionManager or
 * @EnableJpaRepositories. Boot creates them from spring-boot-starter-data-jpa and application.yml.
 */
@SpringBootApplication
@EnableJpaAuditing
public class DataJpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataJpaApplication.class, args);
    }
}
