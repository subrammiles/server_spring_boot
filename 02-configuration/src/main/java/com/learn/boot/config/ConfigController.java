package com.learn.boot.config;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learn.boot.config.payment.PaymentGateway;

/** Shows the effective configuration, so you can watch profiles and overrides change it. */
@RestController
public class ConfigController {
    private final ShopProperties shop;
    private final PaymentGateway payments;
    private final Environment env;

    public ConfigController(ShopProperties shop, PaymentGateway payments, Environment env) {
        this.shop = shop;
        this.payments = payments;
        this.env = env;
    }

    @GetMapping("/config")
    public Map<String, Object> config() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("activeProfiles", Arrays.asList(env.getActiveProfiles()));
        result.put("shop", shop);
        result.put("paymentGateway", payments.getClass().getSimpleName());
        result.put("sampleCharge", payments.charge(shop.currencies().getFirst(), 9.99));
        return result;
    }
}
