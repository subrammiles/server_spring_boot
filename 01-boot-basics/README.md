# 01: Spring Boot basics

In `server_spring` lesson 19 we started Tomcat by hand, registered the `DispatcherServlet`, enabled MVC and
configured JSON. Here the same result takes **one class and one dependency**.

## What Spring Boot adds on top of Spring
| Feature | What it means |
|---|---|
| **Starters** | one dependency per capability (`spring-boot-starter-webmvc` brings Spring MVC, Tomcat, Jackson, logging...) with tested-together versions |
| **Auto-configuration** | beans created for you based on what is on the classpath and in your properties, with conditions so they back off when you define your own |
| **Embedded server** | Tomcat/Jetty inside your app. No WAR deployment. |
| **Externalized config** | `application.properties`/`.yml`, profiles, env vars (module 02) |
| **Executable jar** | `java -jar app.jar` runs everything |
| **Production features** | Actuator health/metrics (module 07) |

## Key pieces in this module
- `@SpringBootApplication` = `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan`.
- `SpringApplication.run(...)` bootstraps everything.
- `ApplicationRunner` / `CommandLineRunner`: run code at startup. `ApplicationReadyEvent` fires when the app can serve traffic.
- **`GreetingAutoConfiguration`**: our own auto-configuration. Read it, because every Boot auto-config works like this:
  registered in `META-INF/spring/...AutoConfiguration.imports`, guarded by `@ConditionalOnProperty`, and `@ConditionalOnMissingBean`
  so you can override it. (Conditions themselves are covered in `server_spring` lesson 09.)
- `banner.txt`: replaces the startup banner (`spring.main.banner-mode=off` hides it).

## Run
```bash
mvn -pl 01-boot-basics spring-boot:run                    # from the repo root
curl 'http://localhost:8080/hello?name=Boot'

mvn -pl 01-boot-basics spring-boot:run -Dspring-boot.run.arguments=--debug   # see the conditions report
mvn -pl 01-boot-basics package && java -jar 01-boot-basics/target/01-boot-basics-1.0.0.jar --server.port=9090
```

## Tests
- `HelloEndpointTest`: full app on a random port plus real HTTP calls (`RestTestClient`).
- `GreetingAutoConfigurationTest`: `ApplicationContextRunner` checks the auto-config under different conditions.

## Try it
- Add `greeting.enabled=false` and read the startup failure message.
  Then add your own `@Bean GreetingService` in `BootBasicsApplication` and watch the auto-config back off.
- Generate a new project at https://start.spring.io and compare its structure with this module.
- Add `spring-boot-devtools` (scope `runtime`, optional) for automatic restarts while coding.
