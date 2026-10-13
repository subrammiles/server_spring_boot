package com.learn.boot.basics;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * @SpringBootTest starts the WHOLE application. RANDOM_PORT starts the real embedded Tomcat on a free
 * port, and RestTestClient sends real HTTP requests to it.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class HelloEndpointTest {

    @Autowired
    RestTestClient client;

    @Test
    void helloUsesTheAutoConfiguredGreetingService() {
        client.get().uri("/hello?name=Boot")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Hello, Boot!");
    }

    @Test
    void defaultName() {
        client.get().uri("/hello").exchange()
                .expectBody(String.class).isEqualTo("Hello, world!");
    }
}
