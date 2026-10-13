package com.learn.boot.config;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

/**
 * TYPE-SAFE configuration: every "shop.*" property bound into one immutable record.
 * Prefer this over many scattered @Value("${...}") fields:
 * - one place documents all settings (and the IDE autocompletes them, thanks to the configuration processor)
 * - values are converted: "30m" -> Duration, "5MB" -> DataSize, lists, maps, nested objects
 * - @Validated: the app REFUSES TO START with invalid config, instead of failing at 3 a.m.
 */
@ConfigurationProperties(prefix = "shop")
@Validated
public record ShopProperties(
        @NotBlank String name,
        @DefaultValue("10") @Min(1) @Max(100) int maxCartItems,
        @DefaultValue("30m") Duration sessionTimeout,
        @DefaultValue("5MB") DataSize maxUploadSize,
        @NotEmpty List<String> currencies,
        Map<String, BigDecimal> shippingRates,
        @Valid Support support) {

    public record Support(@Email String email, String phone) {
    }
}
