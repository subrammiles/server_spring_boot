package com.learn.boot.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.learn.boot.data.report.ReportRepository;

/**
 * TESTCONTAINERS: runs the app against a REAL PostgreSQL in Docker, the same database as production.
 * H2 is convenient but is not PostgreSQL; this catches SQL dialect differences.
 * @ServiceConnection makes Boot point the DataSource at the container automatically.
 * Skipped (not failed) on machines without Docker.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers(disabledWithoutDocker = true)
class PostgresContainerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    JdbcClient jdbc;

    @Autowired
    ReportRepository reports;

    @Test
    void flywayMigrationsAndQueriesWorkOnPostgres() {
        assertThat(jdbc.sql("SELECT version()").query(String.class).single()).startsWith("PostgreSQL 17");
        assertThat(reports.topRatedCourses(1)).first().extracting(ReportRepository.TopCourse::course).isEqualTo("Spring Boot in Depth");
    }
}
