package com.learn.boot.clients;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

import com.learn.boot.clients.posts.PostsClient;

/**
 * Creates a PostsClient bean in the HTTP service group "placeholder".
 * Boot configures the group from spring.http.serviceclient.placeholder.* (base-url, timeouts, headers).
 */
@Configuration(proxyBeanMethods = false)
@ImportHttpServices(group = "placeholder", types = PostsClient.class)
public class ClientsConfig {
}
