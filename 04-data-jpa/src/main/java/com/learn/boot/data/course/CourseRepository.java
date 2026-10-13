package com.learn.boot.data.course;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByNameIgnoreCase(String name);

    /** @EntityGraph: load the reviews in the SAME query (avoids lazy loading and N+1). */
    @EntityGraph(attributePaths = "reviews")
    Optional<Course> findWithReviewsById(Long id);

    @Query(value = """
            SELECT new com.learn.boot.data.course.CourseStats(c.id, c.name, COUNT(r), AVG(r.rating))
            FROM Course c LEFT JOIN c.reviews r
            GROUP BY c.id, c.name""",
            countQuery = "SELECT COUNT(c) FROM Course c")
    Page<CourseStats> findStats(Pageable pageable);
}
