package com.learn.boot.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.unit.DataSize;

import com.learn.boot.config.payment.FakePaymentGateway;
import com.learn.boot.config.payment.PaymentGateway;

/** webEnvironment = NONE: no web server needed to test configuration binding. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ShopPropertiesTest {

    @Autowired
    ShopProperties shop;

    @Autowired
    PaymentGateway payments;

    @Test
    void yamlIsBoundAndConverted() {
        assertThat(shop.name()).isEqualTo("Spring Boot Shop");
        assertThat(shop.maxCartItems()).isEqualTo(20);
        assertThat(shop.sessionTimeout()).isEqualTo(Duration.ofMinutes(45));
        assertThat(shop.maxUploadSize()).isEqualTo(DataSize.ofMegabytes(10));
        assertThat(shop.currencies()).containsExactly("INR", "USD");
        assertThat(shop.shippingRates()).containsEntry("express", new BigDecimal("149.00"));
        assertThat(shop.support().email()).isEqualTo("support@shop.example");
    }

    @Test
    void defaultProfileUsesTheFakeGateway() {
        assertThat(payments).isInstanceOf(FakePaymentGateway.class);
    }
}
