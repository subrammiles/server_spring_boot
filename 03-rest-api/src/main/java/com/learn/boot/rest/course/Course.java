package com.learn.boot.rest.course;

import java.math.BigDecimal;
import java.time.Instant;

/** The internal domain model. Never exposed directly: the API uses CourseResponse. */
public record Course(long id, String title, String category, int durationHours, BigDecimal price, Instant createdAt) {

    public Course withChanges(String title, String category, int durationHours, BigDecimal price) {
        return new Course(id, title, category, durationHours, price, createdAt);
    }
}
