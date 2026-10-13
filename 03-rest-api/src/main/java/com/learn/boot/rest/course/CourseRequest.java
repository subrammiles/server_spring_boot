package com.learn.boot.rest.course;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Input DTO for POST and PUT: only the fields a client may set, with validation rules. */
public record CourseRequest(
        @NotBlank @Size(max = 100) String title,
        @NotBlank String category,
        @Positive @Max(500) int durationHours,
        @NotNull @DecimalMin("0.00") BigDecimal price) {
}
