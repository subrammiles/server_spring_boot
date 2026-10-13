package com.learn.boot.basics;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.learn.boot.basics.greeting.GreetingAutoConfiguration;
import com.learn.boot.basics.greeting.GreetingService;

/**
 * ApplicationContextRunner: a tiny, fast container for testing auto-configurations and
 * conditions with different properties and user beans. This is how Boot tests its own auto-configs.
 */
class GreetingAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GreetingAutoConfiguration.class));

    @Test
    void createsADefaultGreetingService() {
        runner.run(context -> {
            assertThat(context).hasSingleBean(GreetingService.class);
            assertThat(context.getBean(GreetingService.class).greet("x")).isEqualTo("Hello, x!");
        });
    }

    @Test
    void prefixComesFromProperties() {
        runner.withPropertyValues("greeting.prefix=Namaste")
                .run(context -> assertThat(context.getBean(GreetingService.class).greet("Subbu")).isEqualTo("Namaste, Subbu!"));
    }

    @Test
    void canBeSwitchedOff() {
        runner.withPropertyValues("greeting.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(GreetingService.class));
    }

    @Test
    void backsOffWhenTheUserDefinesTheirOwnBean() {
        runner.withUserConfiguration(CustomGreeting.class)
                .run(context -> assertThat(context.getBean(GreetingService.class).greet("x")).isEqualTo("Yo, x!"));
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomGreeting {
        @Bean
        GreetingService myGreetingService() {
            return new GreetingService("Yo");
        }
    }
}
