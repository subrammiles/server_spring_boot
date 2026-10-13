# Spring Boot: from first app to production

A hands-on course in 7 modules. Each module is a **small, standalone Spring Boot application** with:
- a **README.md** explaining the concepts, with commands to run and "Try it" exercises,
- focused example code,
- **tests** that prove it works and show the matching testing technique.

**Prerequisite:** the `server_spring` repository (plain Spring Framework: DI, beans, AOP, transactions, JPA, MVC).
That course shows *how* Spring works. This one shows how Boot does that work for you, and what a real service needs on top.

## Requirements
- Java 21+ (tested on Java 25)
- Docker (optional): only for the PostgreSQL Testcontainers test, which is skipped without it, and for building the image in module 07
- No Maven install needed: use the wrapper `./mvnw` (`mvnw.cmd` on Windows)

## Run
```bash
./mvnw verify                                   # build every module and run all 67 tests
./mvnw -pl 03-rest-api spring-boot:run          # run one module (http://localhost:8080)
./mvnw -pl 04-data-jpa test                     # test one module
```
Or open the root `pom.xml` in IntelliJ/Eclipse/VS Code and run any `*Application` class.

## Roadmap
| # | Module | You will learn |
|---|---|---|
| 01 | [Boot basics](01-boot-basics) | starters, `@SpringBootApplication`, auto-configuration and how to **write your own**, runners, executable jar, `ApplicationContextRunner` |
| 02 | [Configuration](02-configuration) | YAML, `@ConfigurationProperties` (type-safe, validated), relaxed binding, profiles, env vars, secrets, `spring.config.import` |
| 03 | [REST APIs](03-rest-api) | CRUD with DTOs, status codes, PUT vs PATCH, paging, validation, RFC 9457 `ProblemDetail` errors, **API versioning**, OpenAPI/Swagger, `@WebMvcTest` vs full tests |
| 04 | [Data with JPA](04-data-jpa) | Spring Data JPA, **Flyway** migrations, relationships (1-1, 1-n, n-n), inheritance, auditing, `@EntityGraph`/N+1, open-in-view, `JdbcClient`, `@DataJpaTest`, **Testcontainers + PostgreSQL** |
| 05 | [Security](05-security) | Spring Security filter chains, users in a DB, bcrypt, HTTP Basic login, **JWT** resource server, roles, `@PreAuthorize`, CORS/CSRF, security testing |
| 06 | [HTTP clients](06-http-clients) | `RestClient`, declarative `@HttpExchange` clients, timeouts, **`@Retryable`/`@ConcurrencyLimit`** (Spring 7), graceful degradation, stub-server testing |
| 07 | [Production ready](07-production-ready) | Actuator, health and Kubernetes probes, custom health/endpoint, **Micrometer** metrics, Prometheus, Observation API, structured logging, graceful shutdown, virtual threads, Docker image |

## What happened to the old code
The previous single application was split up and fixed:

| Old | Now |
|---|---|
| `RestControler1`, `SimpleControllerAndRequestMaping` | `03-rest-api/.../basics` |
| `courcesproject` (Course, Review, Student, Passport) | `04-data-jpa` with Flyway, DTOs and services |
| `jpainheritancemapping` (subclasses had no `@Entity` and could not be saved) | `04-data-jpa/.../employee` |
| `PersonJDBCDAO` (`JdbcTemplate`, a `RowMapper` that returned `null`) | `04-data-jpa/.../report` (`JdbcClient`) |
| `aop` | covered in `server_spring` lesson 11 |
| `DemoApplicationTests` (expected data that did not exist) | real tests in every module |

## Where to go next
- **Microservices**: Spring Cloud (gateway, config server, service discovery), messaging with Kafka (`server_kafka` repo).
- **Reactive**: Spring WebFlux and R2DBC, for high-concurrency streaming workloads.
- **GraphQL**: `spring-boot-starter-graphql`.
- **Native images**: GraalVM (`./mvnw -Pnative native:compile`) for millisecond startup.

## Versions
Spring Boot 4.1 (Spring Framework 7, Spring Security 7, Spring Data 2026.0, Hibernate 7.4, Jackson 3, Flyway 12,
Testcontainers 2, JUnit 6), Java 21. Versions are managed by `spring-boot-starter-parent`.
