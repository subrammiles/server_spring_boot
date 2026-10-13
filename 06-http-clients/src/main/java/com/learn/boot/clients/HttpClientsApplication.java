package com.learn.boot.clients;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.resilience.annotation.EnableResilientMethods;

/** @EnableResilientMethods turns on Spring Framework 7's built-in @Retryable and @ConcurrencyLimit. */
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableResilientMethods
public class HttpClientsApplication {

    public static void main(String[] args) {
        SpringApplication.run(HttpClientsApplication.class, args);
    }
}
