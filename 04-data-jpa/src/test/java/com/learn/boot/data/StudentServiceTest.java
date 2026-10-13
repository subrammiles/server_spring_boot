package com.learn.boot.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import com.learn.boot.data.common.NotFoundException;
import com.learn.boot.data.student.StudentRepository;
import com.learn.boot.data.student.StudentService;

/** Service + repositories + real database together. @Transactional rolls each test back. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class StudentServiceTest {

    @Autowired
    StudentService service;

    @Autowired
    StudentRepository students;

    @Test
    void registerCascadesThePassport() {
        var profile = service.register("Meera", "ABC12345");

        assertThat(profile.id()).isNotNull();
        assertThat(profile.passportNumber()).isEqualTo("ABC12345");
        assertThat(students.existsByPassportNumber("ABC12345")).isTrue();
    }

    @Test
    void enrolmentIsIdempotent() {
        var profile = service.enroll(2, 2);
        assertThat(profile.courses()).containsExactly("JPA and Hibernate", "Spring Boot in Depth");

        assertThat(service.enroll(2, 2).courses()).hasSize(2);
    }

    @Test
    void duplicatePassportViolatesTheUniqueConstraint() {
        assertThatThrownBy(() -> {
            service.register("Clone", "ERTYUII");
            students.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void unknownIdsAreReported() {
        assertThatThrownBy(() -> service.enroll(1, 999)).isInstanceOf(NotFoundException.class).hasMessage("Course 999 not found");
    }
}
