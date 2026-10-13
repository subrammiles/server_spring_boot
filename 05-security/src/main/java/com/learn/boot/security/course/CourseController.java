package com.learn.boot.security.course;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CourseController {
    private final CourseCatalog catalog;

    public CourseController(CourseCatalog catalog) {
        this.catalog = catalog;
    }

    @GetMapping("/api/courses")
    public List<Map<String, Object>> all() {
        return catalog.all();
    }

    @PostMapping("/api/courses")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> add(@RequestBody Map<String, String> body) {
        return Map.of("id", catalog.add(body.get("title")));
    }

    @DeleteMapping("/api/courses/{id}")
    public ResponseEntity<Void> remove(@PathVariable long id) {
        return catalog.remove(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/api/users/{username}/enrolments")
    public List<String> enrolments(@PathVariable String username) {
        return catalog.enrolmentsOf(username);
    }
}
