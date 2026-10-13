package com.learn.boot.security.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

/** app.jwt.secret must be a base64 key of at least 256 bits. In production it comes from an env var / vault. */
@ConfigurationProperties(prefix = "app.jwt")
@Validated
public record JwtProperties(@NotBlank String secret, @DefaultValue("1h") Duration ttl, @DefaultValue("course-platform") String issuer) {
}
