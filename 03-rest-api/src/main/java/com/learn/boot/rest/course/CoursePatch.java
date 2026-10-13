package com.learn.boot.rest.course;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

/** PATCH = partial update: every field optional, null means "leave unchanged". */
public record CoursePatch(@Size(min = 1, max = 100) String title, @DecimalMin("0.00") BigDecimal price) {
}
