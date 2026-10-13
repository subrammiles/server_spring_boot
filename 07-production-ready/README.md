# 07: Production ready (Actuator, metrics, health, logging, Docker)

Working code isn't enough in production. You also need to know **whether it is healthy**, **what it is doing**,
and you need to **ship and stop** it safely.

## Actuator endpoints (`/actuator/...`)
| Endpoint | Purpose |
|---|---|
| `health` | UP/DOWN with per-component details. Load balancers and Kubernetes use it |
| `health/liveness`, `health/readiness` | Kubernetes probes: "restart me" vs "don't send me traffic yet" |
| `info` | build version (from `build-info`), Java version, custom `info.*` properties |
| `metrics`, `metrics/{name}` | browse metrics |
| `prometheus` | all metrics in Prometheus format, for scraping and Grafana dashboards |
| `loggers` | view or **change log levels at runtime**, without a restart |
| `features` | our **custom endpoint** (`@Endpoint`, `@ReadOperation`, `@WriteOperation`) |

Only `health` is exposed by default. Expose endpoints deliberately (`management.endpoints.web.exposure.include`),
and in production put them behind security or on a separate port (`management.server.port`).

## Concepts
- **Custom health indicator** (`PaymentProviderHealthIndicator`): a failing dependency turns health DOWN and returns HTTP 503.
- **Business metrics with Micrometer** (`OrderService`): `Counter` (orders by outcome), `DistributionSummary`
  (order value), `Gauge` (open orders). Keep tags low-cardinality: product type yes, user id never.
- **Observation API**: one call produces a timer metric, a trace span (once a tracer such as OpenTelemetry is added)
  and correlated logs.
- **Structured logging**: `--spring.profiles.active=json` prints ECS JSON logs that log platforms can index.
- **Graceful shutdown**: on SIGTERM, stop accepting requests and finish in-flight ones (`timeout-per-shutdown-phase`).
- **Virtual threads**: `spring.threads.virtual.enabled=true` (Java 21+) gives cheap blocking I/O for request handling.
- **Container image** (`Dockerfile`): multi-stage build, **layered jar** (dependencies cached separately from your code),
  non-root user, container-aware memory (`MaxRAMPercentage`).
  The no-Dockerfile alternative: `mvn -pl 07-production-ready spring-boot:build-image` (Cloud Native Buildpacks).

## Run
```bash
mvn -pl 07-production-ready spring-boot:run
curl localhost:8080/actuator/health
curl -X POST localhost:8080/api/orders -H 'Content-Type: application/json' -d '{"product":"book","amount":499}'
curl localhost:8080/actuator/metrics/shop.orders
curl -s localhost:8080/actuator/prometheus | grep shop_
curl -X POST localhost:8080/actuator/features/payment-outage -H 'Content-Type: application/json' -d '{"enabled":true}'
curl -i localhost:8080/actuator/health          # now 503 DOWN
curl -X POST localhost:8080/actuator/loggers/com.learn.boot -H 'Content-Type: application/json' -d '{"configuredLevel":"DEBUG"}'

mvn -pl 07-production-ready spring-boot:run -Dspring-boot.run.profiles=json   # JSON logs

# Container (from the repository root)
docker build -f 07-production-ready/Dockerfile -t production-ready .
docker run -p 8080:8080 production-ready
```

## Try it
- Add `micrometer-tracing-bridge-otel` and an OTLP exporter, then view traces in Jaeger or Grafana Tempo.
- Run Prometheus + Grafana with Docker Compose and chart `shop_orders_total`.
