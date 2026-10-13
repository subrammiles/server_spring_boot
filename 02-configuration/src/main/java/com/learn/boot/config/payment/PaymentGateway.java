package com.learn.boot.config.payment;

public interface PaymentGateway {
    String charge(String currency, double amount);
}
