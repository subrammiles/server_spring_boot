package com.learn.boot.ops.health;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * A custom health check, shown under /actuator/health as "paymentProvider".
 * Boot already adds checks for the database, disk space, Redis, Kafka... when they are present.
 */
@Component
public class PaymentProviderHealthIndicator implements HealthIndicator {
    private final PaymentProviderClient client;

    public PaymentProviderHealthIndicator(PaymentProviderClient client) {
        this.client = client;
    }

    @Override
    public Health health() {
        return client.ping()
                ? Health.up().withDetail("provider", "FakePay").build()
                : Health.down().withDetail("provider", "FakePay").withDetail("error", "not reachable").build();
    }
}
