package com.learn.boot.basics.greeting;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * How Spring Boot's "magic" works, in 15 lines. Boot ships hundreds of classes like this one.
 *
 * - It is listed in META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports,
 *   so Boot loads it automatically (no component scan needed).
 * - @ConditionalOnProperty: switch it off with greeting.enabled=false.
 * - @ConditionalOnMissingBean: if YOU define a GreetingService bean, yours wins and this backs off.
 *   That is the rule for all Boot defaults: you can always override them.
 *
 * See it in action: run with --debug and look for GreetingAutoConfiguration in the CONDITIONS EVALUATION REPORT.
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "greeting", name = "enabled", havingValue = "true", matchIfMissing = true)
public class GreetingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public GreetingService greetingService(@Value("${greeting.prefix:Hello}") String prefix) {
        return new GreetingService(prefix);
    }
}
