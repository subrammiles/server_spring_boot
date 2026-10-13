package com.learn.boot.rest.course;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * API version 2 changed the shape: price became an object with currency and tax.
 * Old clients keep getting version 1; new clients send "API-Version: 2.0".
 */
public record CourseResponseV2(long id, String title, String category, int durationHours, Price price) {

    public record Price(BigDecimal amount, String currency, BigDecimal amountWithGst) {
    }

    public static CourseResponseV2 from(Course c) {
        BigDecimal withGst = c.price().multiply(new BigDecimal("1.18")).setScale(2, RoundingMode.HALF_UP);
        return new CourseResponseV2(c.id(), c.title(), c.category(), c.durationHours(), new Price(c.price(), "INR", withGst));
    }
}
