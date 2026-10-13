package com.learn.boot.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;

import com.learn.boot.data.course.Course;
import com.learn.boot.data.course.CourseRepository;
import com.learn.boot.data.course.CourseStats;
import com.learn.boot.data.employee.EmployeeRepository;
import com.learn.boot.data.employee.FullTimeEmployee;
import com.learn.boot.data.employee.PartTimeEmployee;
import com.learn.boot.data.student.StudentRepository;

import jakarta.persistence.EntityManagerFactory;

/**
 * SLICE TEST: @DataJpaTest starts only JPA + the DataSource + Flyway (no web layer, no services).
 * Every test runs in a transaction that is rolled back afterwards.
 */
@DataJpaTest
@TestPropertySource(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
class RepositoryTest {

    @Autowired
    CourseRepository courses;

    @Autowired
    StudentRepository students;

    @Autowired
    EmployeeRepository employees;

    @Autowired
    TestEntityManager em;   // a test helper around the EntityManager

    @Autowired
    EntityManagerFactory emf;

    @Test
    void flywaySeedDataIsThere() {
        assertThat(courses.count()).isEqualTo(3);
        assertThat(courses.findByNameIgnoreCase("spring boot in depth")).isPresent();
    }

    @Test
    void aggregateQueryWithPagingAndSorting() {
        var page = courses.findStats(PageRequest.of(0, 2, Sort.by("name")));

        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getContent()).extracting(CourseStats::name).containsExactly("JPA and Hibernate", "Microservices");
        CourseStats microservices = page.getContent().get(1);
        assertThat(microservices.reviewCount()).isZero();
        assertThat(microservices.averageRating()).isNull();
    }

    @Test
    void entityGraphLoadsReviewsInOneQuery() {
        em.clear();
        Statistics stats = emf.unwrap(SessionFactory.class).getStatistics();
        stats.clear();

        Course course = courses.findWithReviewsById(1L).orElseThrow();
        assertThat(course.getReviews()).hasSize(2);   // no extra query: already fetched
        assertThat(stats.getPrepareStatementCount()).isEqualTo(1);
    }

    @Test
    void cascadeSavesNewReviewsAndAuditingFillsTimestamps() {
        Course course = courses.save(new Course("Kotlin"));
        course.addReview(5, "Loved it");
        em.flush();
        em.clear();

        Course reloaded = courses.findWithReviewsById(course.getId()).orElseThrow();
        assertThat(reloaded.getReviews()).singleElement().extracting("rating").isEqualTo(5);
        assertThat(reloaded.getCreatedAt()).isNotNull();
    }

    @Test
    void derivedQueryAcrossManyToMany() {
        assertThat(students.findByCoursesNameOrderByName("Spring Boot in Depth")).extracting("name").containsExactly("Ravi", "Subbu");
        assertThat(students.existsByPassportNumber("ERTYUII")).isTrue();
    }

    @Test
    void inheritanceQueriesArePolymorphic() {
        employees.save(new PartTimeEmployee("Zoya", new BigDecimal("500")));

        assertThat(employees.findAllByOrderByName()).hasSize(3)
                .hasAtLeastOneElementOfType(FullTimeEmployee.class)
                .hasAtLeastOneElementOfType(PartTimeEmployee.class);
        assertThat(employees.findPartTimers()).extracting("name").containsExactly("Kiran", "Zoya");
    }
}
