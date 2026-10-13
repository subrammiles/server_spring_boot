package com.learn.boot.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/** @ConfigurationPropertiesScan registers every @ConfigurationProperties class in this package. */
@SpringBootApplication
@ConfigurationPropertiesScan
public class ConfigurationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigurationApplication.class, args);
    }
}
