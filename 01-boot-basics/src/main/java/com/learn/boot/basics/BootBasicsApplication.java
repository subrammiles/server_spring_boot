package com.learn.boot.basics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @SpringBootApplication = three annotations in one:
 *   @Configuration            this class can declare @Bean methods
 *   @EnableAutoConfiguration  let Boot configure beans based on the classpath and properties
 *   @ComponentScan            scan this package and its sub-packages
 *
 * SpringApplication.run creates the ApplicationContext, applies auto-configuration,
 * starts the embedded Tomcat and runs CommandLineRunner/ApplicationRunner beans.
 */
@SpringBootApplication
public class BootBasicsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootBasicsApplication.class, args);
    }
}
