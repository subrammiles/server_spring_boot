# 06: Calling other services (HTTP clients and resilience)

Backends call other backends all the time: payment providers, other microservices, public APIs.
This module calls https://jsonplaceholder.typicode.com in two styles and makes the calls robust.

| Endpoint | What happens |
|---|---|
| `GET /api/posts/{id}`, `POST /api/posts` | declarative client (`PostsClient`) |
| `GET /api/authors/{id}` | combines a user (`UsersGateway`, RestClient) with their posts (`PostsClient`) |

## Two client styles
- **`RestClient`** (`UsersGateway`): fluent and imperative. `get().uri(...).retrieve().onStatus(...).body(User.class)`.
  Build it from Boot's auto-configured `RestClient.Builder` to inherit timeouts, JSON, SSL and observability.
  It replaces the old `RestTemplate`.
- **HTTP interface** (`PostsClient`): an annotated interface that Spring implements. Registered with
  `@ImportHttpServices(group = "placeholder")` and configured under `spring.http.serviceclient.placeholder.*`.

## Making remote calls robust
Remote calls fail: slow networks, restarts, overload. Plan for it:
- **Timeouts** (`spring.http.clients.connect-timeout/read-timeout`). Without them one slow service can hang all your threads.
- **Retries for transient errors only** (Spring Framework 7's `@Retryable` + `@EnableResilientMethods`):
  5xx and network errors, with exponential backoff. **Never retry 4xx** (the request itself is wrong), and only retry
  operations that are safe to repeat (idempotent).
- **Concurrency limits** (`@ConcurrencyLimit`): cap parallel calls to a fragile dependency (bulkhead pattern).
- **Error mapping**: upstream 404 becomes our 404. An upstream failure after retries becomes **502 Bad Gateway**.
- **Graceful degradation**: if the optional posts call fails, the author is still returned with `"degraded": true`.
- For **circuit breakers** and rate limiters, add Resilience4j (`spring-cloud-starter-circuitbreaker-resilience4j`).

## Testing remote calls
`HttpClientsTest` starts a **local stub server** (`StubServer`, the JDK's built-in HTTP server) and points the clients
at it with `@DynamicPropertySource`. The tests are fast and offline, and failures can be scripted (503, 503, then 200)
to prove retries and degradation. Alternatives: WireMock, `MockRestServiceServer`, Testcontainers (MockServer).

## Run
```bash
mvn -pl 06-http-clients spring-boot:run          # needs internet
curl localhost:8080/api/posts/1
curl localhost:8080/api/authors/1
curl localhost:8080/api/authors/999              # 404 mapped from upstream
```

## Try it
- Set `clients.users.base-url` to `http://localhost:9` (nothing listening) and watch the retries in the logs.
- Make `AuthorService` fetch the user and the posts **in parallel** (`CompletableFuture` + virtual threads).
