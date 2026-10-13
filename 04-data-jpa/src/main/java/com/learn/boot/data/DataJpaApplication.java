package com.learn.boot.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;

/**
 * Compare with server_spring lessons 17-18: no DataSource, EntityManagerFactory, TransactionManager or
 * @EnableJpaRepositories. Boot creates them from spring-boot-starter-data-jpa and application.yml.
 *
 * VIA_DTO: Page results are serialized as a stable {"content": [...], "page": {...}} JSON shape
 * instead of dumping Spring Data's internal PageImpl class.
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
public class DataJpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataJpaApplication.class, args);
    }
}
