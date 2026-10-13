package com.learn.boot.data.course;

/** A read model computed by the database (JPQL constructor expression). */
public record CourseStats(Long id, String name, long reviewCount, Double averageRating) {
}
