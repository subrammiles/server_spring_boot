package com.learn.boot.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

/** Validation turns bad configuration into a startup failure with a clear message. */
class InvalidConfigurationTest {

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(ShopProperties.class)
    static class Config {
    }

    private final ApplicationContextRunner runner = new ApplicationContextRunner().withUserConfiguration(Config.class);

    @Test
    void validConfigurationStarts() {
        runner.withPropertyValues("shop.name=Ok", "shop.currencies=INR")
                .run(context -> assertThat(context).hasNotFailed());
    }

    @Test
    void invalidConfigurationRefusesToStart() {
        runner.withPropertyValues("shop.name=", "shop.max-cart-items=500", "shop.currencies=INR", "shop.support.email=not-an-email")
                .run(context -> assertThat(context).hasFailed()
                        .getFailure().rootCause().hasMessageContainingAll("name", "maxCartItems", "support.email"));
    }
}
