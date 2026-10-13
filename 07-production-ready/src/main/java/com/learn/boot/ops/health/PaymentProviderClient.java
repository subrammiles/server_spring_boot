package com.learn.boot.ops.health;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.stereotype.Component;

/** Stands in for a real dependency (a payment API). Its state can be flipped to simulate an outage. */
@Component
public class PaymentProviderClient {
    private final AtomicBoolean reachable = new AtomicBoolean(true);

    public boolean ping() {
        return reachable.get();
    }

    public void simulateOutage(boolean down) {
        reachable.set(!down);
    }
}
