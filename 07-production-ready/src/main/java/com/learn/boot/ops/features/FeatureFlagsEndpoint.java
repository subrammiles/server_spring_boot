package com.learn.boot.ops.features;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

import com.learn.boot.ops.health.PaymentProviderClient;

/**
 * A CUSTOM actuator endpoint at /actuator/features:
 *   GET  /actuator/features              list flags
 *   POST /actuator/features/{name}       {"enabled": true}
 * The flag "payment-outage" lets you watch /actuator/health turn DOWN.
 */
@Component
@Endpoint(id = "features")
public class FeatureFlagsEndpoint {
    private final Map<String, Boolean> flags = new ConcurrentHashMap<>(Map.of("new-checkout", false, "payment-outage", false));
    private final PaymentProviderClient payments;

    public FeatureFlagsEndpoint(PaymentProviderClient payments) {
        this.payments = payments;
    }

    @ReadOperation
    public Map<String, Boolean> flags() {
        return new TreeMap<>(flags);
    }

    @WriteOperation
    public Map<String, Boolean> set(@Selector String name, boolean enabled) {
        flags.put(name, enabled);
        if (name.equals("payment-outage")) {
            payments.simulateOutage(enabled);
        }
        return flags();
    }
}
