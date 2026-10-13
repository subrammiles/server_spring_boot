package com.learn.boot.rest.course;

import java.math.BigDecimal;
import java.time.Instant;

/** Output DTO (API version 1). Decoupled from the domain so the domain can change without breaking clients. */
public record CourseResponse(long id, String title, String category, int durationHours, BigDecimal price, Instant createdAt) {

    public static CourseResponse from(Course c) {
        return new CourseResponse(c.id(), c.title(), c.category(), c.durationHours(), c.price(), c.createdAt());
    }
}
