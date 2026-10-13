package com.learn.boot.config.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Only in prod. The API key is a SECRET: it is not in any committed file. It comes from the
 * environment variable SHOP_PAYMENT_API_KEY (relaxed binding maps it to shop.payment.api-key).
 */
@Component
@Profile("prod")
public class RealPaymentGateway implements PaymentGateway {
    private final String apiKey;

    public RealPaymentGateway(@Value("${shop.payment.api-key}") String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public String charge(String currency, double amount) {
        return "REAL charge of " + amount + " " + currency + " with key ****" + apiKey.substring(Math.max(0, apiKey.length() - 4));
    }
}
