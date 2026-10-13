package com.learn.boot.clients.users;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "clients.users")
public record UsersProperties(URI baseUrl) {
}
