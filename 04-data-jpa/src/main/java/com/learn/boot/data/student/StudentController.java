package com.learn.boot.data.student;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.learn.boot.data.student.StudentService.StudentProfile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentProfile register(@Valid @RequestBody NewStudent request) {
        return service.register(request.name(), request.passportNumber());
    }

    @GetMapping("/{id}")
    public StudentProfile get(@PathVariable long id) {
        return service.profile(id);
    }

    /** PUT is idempotent: enrolling twice has the same effect as once. */
    @PutMapping("/{id}/courses/{courseId}")
    public StudentProfile enroll(@PathVariable long id, @PathVariable long courseId) {
        return service.enroll(id, courseId);
    }

    @GetMapping
    public List<String> byCourse(@RequestParam String course) {
        return service.studentsIn(course);
    }

    public record NewStudent(@NotBlank String name, @Pattern(regexp = "[A-Z0-9]{6,20}") String passportNumber) {
    }
}
