package com.learn.boot.clients;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import com.learn.boot.clients.StubServer.Reply;
import com.learn.boot.clients.users.UsersGateway;

/**
 * The whole app, with both remote APIs replaced by a local stub.
 * @DynamicPropertySource points the clients at the stub's random port before the context starts.
 */
@SpringBootTest
@AutoConfigureMockMvc
class HttpClientsTest {

    static final StubServer stub = new StubServer();

    @DynamicPropertySource
    static void remoteUrls(DynamicPropertyRegistry registry) {
        registry.add("spring.http.serviceclient.placeholder.base-url", stub::baseUrl);
        registry.add("clients.users.base-url", stub::baseUrl);
    }

    @AfterAll
    static void stopStub() {
        stub.stop();
    }

    @Autowired
    MockMvcTester mvc;

    @Autowired
    UsersGateway usersGateway;

    @BeforeEach
    void reset() {
        stub.reset();
        stub.reply("GET", "/users/1", new Reply(200, "{\"id\":1,\"name\":\"Leanne\",\"email\":\"leanne@example.com\",\"phone\":\"ignored\"}"));
        stub.reply("GET", "/posts?userId=1", new Reply(200, "[{\"id\":1,\"userId\":1,\"title\":\"First\",\"body\":\"b\"}]"));
    }

    @Test
    void declarativeClientFetchesAPost() {
        stub.reply("GET", "/posts/5", new Reply(200, "{\"id\":5,\"userId\":1,\"title\":\"Hello\",\"body\":\"World\"}"));

        assertThat(mvc.get().uri("/api/posts/5")).hasStatusOk().bodyJson().extractingPath("$.title").isEqualTo("Hello");
    }

    @Test
    void declarativeClientSendsJsonBodies() {
        stub.reply("POST", "/posts", new Reply(201, "{\"id\":101,\"userId\":1,\"title\":\"New\",\"body\":\"text\"}"));

        assertThat(mvc.post().uri("/api/posts").contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":1,\"title\":\"New\",\"body\":\"text\"}"))
                .hasStatus(HttpStatus.CREATED).bodyJson().extractingPath("$.id").isEqualTo(101);
        assertThat(stub.requests()).anyMatch(r -> r.startsWith("POST /posts") && r.contains("\"title\":\"New\""));
    }

    @Test
    void aggregatesTwoServices() {
        assertThat(mvc.get().uri("/api/authors/1")).hasStatusOk().bodyJson()
                .isLenientlyEqualTo("{\"name\":\"Leanne\",\"postTitles\":[\"First\"],\"degraded\":false}");
    }

    @Test
    void transientServerErrorsAreRetried() {
        stub.reply("GET", "/users/9",
                new Reply(503, "{}"), new Reply(503, "{}"),
                new Reply(200, "{\"id\":9,\"name\":\"Retry\",\"email\":\"r@example.com\"}"));
        stub.reply("GET", "/posts?userId=9", new Reply(200, "[]"));
        int before = usersGateway.attempts();

        assertThat(mvc.get().uri("/api/authors/9")).hasStatusOk().bodyJson().extractingPath("$.name").isEqualTo("Retry");
        assertThat(usersGateway.attempts() - before).isEqualTo(3);
    }

    @Test
    void persistentFailureBecomes502AfterRetries() {
        stub.reply("GET", "/users/7", new Reply(500, "{}"));
        int before = usersGateway.attempts();

        assertThat(mvc.get().uri("/api/authors/7")).hasStatus(HttpStatus.BAD_GATEWAY);
        assertThat(usersGateway.attempts() - before).isEqualTo(3);   // 1 call + 2 retries
    }

    @Test
    void clientErrorsAreNotRetried() {
        int before = usersGateway.attempts();

        assertThat(mvc.get().uri("/api/authors/404")).hasStatus(HttpStatus.NOT_FOUND)
                .bodyJson().extractingPath("$.detail").isEqualTo("User 404 not found upstream");
        assertThat(usersGateway.attempts() - before).isEqualTo(1);
    }

    @Test
    void optionalDataDegradesGracefully() {
        stub.reply("GET", "/posts?userId=1", new Reply(500, "{}"));

        assertThat(mvc.get().uri("/api/authors/1")).hasStatusOk().bodyJson()
                .isLenientlyEqualTo("{\"name\":\"Leanne\",\"postTitles\":[],\"degraded\":true}");
    }
}
