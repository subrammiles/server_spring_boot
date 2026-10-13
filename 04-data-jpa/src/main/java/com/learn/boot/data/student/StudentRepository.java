package com.learn.boot.data.student;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @EntityGraph(attributePaths = {"passport", "courses"})
    Optional<Student> findDetailedById(Long id);

    /** Derived query that navigates a relationship: student -> courses -> name. */
    List<Student> findByCoursesNameOrderByName(String courseName);

    boolean existsByPassportNumber(String number);
}
