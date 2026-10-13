# Multi-stage build: compile in a JDK image, run in a small JRE image.
# Build from the REPOSITORY ROOT:   docker build -f 07-production-ready/Dockerfile -t production-ready .
# Run:                              docker run -p 8080:8080 production-ready

FROM eclipse-temurin:21-jdk AS build
WORKDIR /src
COPY . .
RUN apt-get update && apt-get install -y --no-install-recommends maven && rm -rf /var/lib/apt/lists/*
RUN mvn -q -pl 07-production-ready -am package -DskipTests
# Split the fat jar into layers (dependencies change rarely, our code often) for faster rebuilds and pulls
RUN java -Djarmode=tools -jar 07-production-ready/target/07-production-ready-1.0.0.jar extract --layers --launcher --destination /extracted

FROM eclipse-temurin:21-jre
WORKDIR /app
# Never run as root inside the container
RUN useradd --system --uid 1001 spring
USER spring
COPY --from=build /extracted/dependencies/ ./
COPY --from=build /extracted/spring-boot-loader/ ./
COPY --from=build /extracted/snapshot-dependencies/ ./
COPY --from=build /extracted/application/ ./
EXPOSE 8080
# Health: point your platform's probes at /actuator/health/liveness and /actuator/health/readiness
# (the JRE image has no curl/wget for a Docker HEALTHCHECK, and Kubernetes ignores HEALTHCHECK anyway).
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "org.springframework.boot.loader.launch.JarLauncher"]
