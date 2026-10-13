package com.learn.boot.config.payment;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** Used everywhere EXCEPT prod: developers and tests never charge real cards. */
@Component
@Profile("!prod")
public class FakePaymentGateway implements PaymentGateway {
    @Override
    public String charge(String currency, double amount) {
        return "FAKE charge of " + amount + " " + currency;
    }
}
