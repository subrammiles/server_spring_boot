package com.learn.boot.data.report;

import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/**
 * JPA is not the only option. For reporting queries, plain SQL with JdbcClient is often clearer
 * and faster. Boot auto-configures JdbcClient from the same DataSource.
 * (This replaces the old PersonJDBCDAO built on JdbcTemplate.)
 */
@Repository
public class ReportRepository {
    private final JdbcClient jdbc;

    public ReportRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<TopCourse> topRatedCourses(int minReviews) {
        return jdbc.sql("""
                        SELECT c.name AS course, COUNT(r.id) AS reviews, ROUND(AVG(r.rating), 2) AS average_rating
                        FROM course c JOIN review r ON r.course_id = c.id
                        GROUP BY c.name
                        HAVING COUNT(r.id) >= :minReviews
                        ORDER BY average_rating DESC, course""")
                .param("minReviews", minReviews)
                .query(TopCourse.class)
                .list();
    }

    public record TopCourse(String course, long reviews, double averageRating) {
    }
}
