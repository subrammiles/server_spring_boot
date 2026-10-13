package com.learn.boot.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.learn.boot.config.payment.PaymentGateway;
import com.learn.boot.config.payment.RealPaymentGateway;

class ProfilesTest {

    @Nested
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
    @ActiveProfiles("dev")
    class Dev {
        @Autowired
        ShopProperties shop;

        @Test
        void devFileOverridesOnlyWhatItDeclares() {
            assertThat(shop.name()).endsWith("(DEV)");
            assertThat(shop.shippingRates().get("standard")).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(shop.shippingRates().get("express")).isEqualByComparingTo("149.00");   // inherited
        }
    }

    /** properties = ... plays the role of the environment variable in a real deployment. */
    @Nested
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
            properties = "SHOP_PAYMENT_API_KEY=sk_test_12345678")
    @ActiveProfiles("prod")
    class Prod {
        @Autowired
        ShopProperties shop;

        @Autowired
        PaymentGateway payments;

        @Test
        void prodUsesRealGatewayWithTheInjectedSecret() {
            assertThat(shop.maxCartItems()).isEqualTo(50);
            assertThat(payments).isInstanceOf(RealPaymentGateway.class);
            assertThat(payments.charge("INR", 1)).endsWith("****5678");
        }
    }
}
