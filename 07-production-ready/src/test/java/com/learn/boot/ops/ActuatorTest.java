package com.learn.boot.ops;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.micrometer.metrics.test.autoconfigure.AutoConfigureMetrics;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import com.learn.boot.ops.health.PaymentProviderClient;

import io.micrometer.core.instrument.MeterRegistry;

/** @AutoConfigureMetrics: metric exporters (Prometheus) are switched off in tests unless you ask for them. */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureMetrics
class ActuatorTest {

    @Autowired
    MockMvcTester mvc;

    @Autowired
    MeterRegistry meters;

    @Autowired
    PaymentProviderClient payments;

    @AfterEach
    void restore() {
        payments.simulateOutage(false);
    }

    @Test
    void healthIncludesTheCustomIndicatorAndKubernetesProbes() {
        assertThat(mvc.get().uri("/actuator/health")).hasStatusOk().bodyJson()
                .isLenientlyEqualTo("{\"status\":\"UP\",\"components\":{\"paymentProvider\":{\"status\":\"UP\"}}}");
        assertThat(mvc.get().uri("/actuator/health/liveness")).hasStatusOk();
        assertThat(mvc.get().uri("/actuator/health/readiness")).hasStatusOk();
    }

    @Test
    void failingDependencyTurnsHealthDownWith503() {
        assertThat(mvc.post().uri("/actuator/features/payment-outage")
                .contentType(MediaType.APPLICATION_JSON).content("{\"enabled\":true}")).hasStatusOk();

        assertThat(mvc.get().uri("/actuator/health")).hasStatus(HttpStatus.SERVICE_UNAVAILABLE)
                .bodyJson().extractingPath("$.components.paymentProvider.details.error").isEqualTo("not reachable");
    }

    @Test
    void customEndpointReadsFlags() {
        assertThat(mvc.get().uri("/actuator/features")).hasStatusOk()
                .bodyJson().extractingPath("$.new-checkout").isEqualTo(false);
    }

    @Test
    void businessMetricsAreRecordedAndExportedToPrometheus() {
        double before = meters.counter("shop.orders", "outcome", "placed").count();

        assertThat(mvc.post().uri("/api/orders").contentType(MediaType.APPLICATION_JSON)
                .content("{\"product\":\"book\",\"amount\":499}")).hasStatus(HttpStatus.CREATED);
        assertThat(mvc.post().uri("/api/orders").contentType(MediaType.APPLICATION_JSON)
                .content("{\"product\":\"book\",\"amount\":0}")).hasStatus(HttpStatus.BAD_REQUEST);

        assertThat(meters.counter("shop.orders", "outcome", "placed").count()).isEqualTo(before + 1);
        assertThat(meters.find("shop.order.processing").timer()).isNotNull();   // from the Observation

        assertThat(mvc.get().uri("/actuator/metrics/shop.orders")).hasStatusOk();
        assertThat(mvc.get().uri("/actuator/prometheus")).hasStatusOk().bodyText()
                .contains("shop_orders_total{application=\"production-ready\",outcome=\"placed\"}")
                .contains("shop_orders_value_inr_count");
    }

    @Test
    void infoShowsBuildAndCustomInfo() {
        assertThat(mvc.get().uri("/actuator/info")).hasStatusOk().bodyJson()
                .isLenientlyEqualTo("{\"build\":{\"artifact\":\"07-production-ready\"},\"app\":{\"team\":\"platform@example.com\"}}");
    }

    @Test
    void unexposedEndpointsAreNotReachable() {
        assertThat(mvc.get().uri("/actuator/env")).hasStatus(HttpStatus.NOT_FOUND);
    }
}
