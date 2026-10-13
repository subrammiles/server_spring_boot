package com.learn.boot.clients.users;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.HttpStatus;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

/**
 * IMPERATIVE client with RestClient's fluent API (the modern replacement for RestTemplate).
 * Build it from Boot's auto-configured RestClient.Builder so it inherits timeouts, JSON setup,
 * observability (metrics/tracing) and SSL settings.
 */
@Component
public class UsersGateway {
    private final RestClient restClient;
    private final AtomicInteger attempts = new AtomicInteger();

    public UsersGateway(RestClient.Builder builder, UsersProperties props) {
        this.restClient = builder.baseUrl(props.baseUrl().toString()).build();
    }

    /**
     * RETRY transient failures only: 5xx answers and network errors (timeouts, refused connections).
     * Never retry 4xx: asking again will not make a bad request good.
     * Exponential backoff: wait 100 ms, then 200 ms.
     *
     * CONCURRENCY LIMIT: at most 10 parallel calls, so a slow upstream cannot exhaust our threads.
     */
    @Retryable(includes = {HttpServerErrorException.class, ResourceAccessException.class},
            maxRetries = 2, delay = 100, multiplier = 2)
    @ConcurrencyLimit(10)
    public User findUser(long id) {
        attempts.incrementAndGet();
        return restClient.get()
                .uri("/users/{id}", id)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND), (request, response) -> {
                    throw new UserNotFoundException(id);
                })
                .body(User.class);
    }

    public int attempts() {
        return attempts.get();
    }
}
