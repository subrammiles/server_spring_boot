package com.learn.boot.ops.orders;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

/**
 * Business METRICS: technical metrics (CPU, memory, HTTP latency) come for free with Actuator,
 * but only you know what matters to the business: orders placed, rejected, order value...
 */
@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final MeterRegistry meters;
    private final ObservationRegistry observations;
    private final DistributionSummary orderValue;
    private final AtomicInteger openOrders = new AtomicInteger();

    public OrderService(MeterRegistry meters, ObservationRegistry observations) {
        this.meters = meters;
        this.observations = observations;
        this.orderValue = DistributionSummary.builder("shop.orders.value")
                .description("Value of placed orders")
                .baseUnit("inr")
                .register(meters);
        // A gauge samples a current value (here: orders still being processed)
        meters.gauge("shop.orders.open", openOrders);
    }

    public String place(String product, double amount) {
        // An Observation = timer metric + trace span + log correlation, in one API
        return Observation.createNotStarted("shop.order.processing", observations)
                .lowCardinalityKeyValue("product", product)   // keep tag values few: never user ids
                .observe(() -> {
                    openOrders.incrementAndGet();
                    try {
                        if (amount <= 0) {
                            counter("rejected").increment();
                            log.warn("Rejected order for {} with amount {}", product, amount);
                            throw new IllegalArgumentException("amount must be positive");
                        }
                        counter("placed").increment();
                        orderValue.record(amount);
                        log.info("Order placed: product={} amount={}", product, amount);
                        return "order accepted";
                    } finally {
                        openOrders.decrementAndGet();
                    }
                });
    }

    private Counter counter(String outcome) {
        return Counter.builder("shop.orders").tag("outcome", outcome).description("Orders by outcome").register(meters);
    }
}
