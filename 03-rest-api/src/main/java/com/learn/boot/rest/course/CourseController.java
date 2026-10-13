package com.learn.boot.rest.course;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * A complete REST resource:
 *
 *   GET    /api/courses?category=&q=&page=0&size=10&sort=title   search + paging + sorting
 *   GET    /api/courses/{id}          one course (version 1 or 2, chosen by the API-Version header)
 *   POST   /api/courses               create -> 201 Created + Location
 *   PUT    /api/courses/{id}          full replace
 *   PATCH  /api/courses/{id}          partial update
 *   DELETE /api/courses/{id}          -> 204 No Content
 *
 * Constraints on @RequestParam/@PathVariable (@Min, @Max below) are validated by Spring MVC itself
 * (since Spring 6.1) and produce a 400. Do NOT also put @Validated on the controller class: that adds
 * an AOP proxy that throws ConstraintViolationException instead, which surfaces as a 500.
 */
@RestController
@RequestMapping("/api/courses")
@Tag(name = "Courses", description = "Course catalog")
public class CourseController {
    private final CourseService service;

    public CourseController(CourseService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Search courses with paging and sorting")
    public PageResponse<CourseResponse> search(@RequestParam(required = false) String category,
                                               @RequestParam(required = false) String q,
                                               @RequestParam(defaultValue = "0") @Min(0) int page,
                                               @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
                                               @RequestParam(defaultValue = "title") String sort) {
        return service.search(category, q, page, size, sort).map(CourseResponse::from);
    }

    @GetMapping(path = "/{id}", version = "1.0")
    @Operation(summary = "Get one course (v1: flat price)")
    public CourseResponse get(@PathVariable long id) {
        return CourseResponse.from(service.get(id));
    }

    @GetMapping(path = "/{id}", version = "2.0")
    @Operation(summary = "Get one course (v2: price object with GST)")
    public CourseResponseV2 getV2(@PathVariable long id) {
        return CourseResponseV2.from(service.get(id));
    }

    @PostMapping
    @Operation(summary = "Create a course")
    public ResponseEntity<CourseResponse> create(@Valid @RequestBody CourseRequest request) {
        Course created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(CourseResponse.from(created));
    }

    @PutMapping("/{id}")
    public CourseResponse replace(@PathVariable long id, @Valid @RequestBody CourseRequest request) {
        return CourseResponse.from(service.replace(id, request));
    }

    @PatchMapping("/{id}")
    public CourseResponse patch(@PathVariable long id, @Valid @RequestBody CoursePatch patch) {
        return CourseResponse.from(service.patch(id, patch));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
