package com.learn.boot.data.course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.learn.boot.data.course.CourseService.CourseDetails;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseService service;

    public CourseController(CourseService service) {
        this.service = service;
    }

    /** Spring Data web support turns ?page=0&size=5&sort=name,desc into a Pageable. */
    @GetMapping
    public Page<CourseStats> list(@PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return service.stats(pageable);
    }

    @GetMapping("/{id}")
    public CourseDetails get(@PathVariable long id) {
        return service.details(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseDetails create(@Valid @RequestBody NewCourse request) {
        return service.create(request.name());
    }

    @PostMapping("/{id}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public CourseDetails addReview(@PathVariable long id, @Valid @RequestBody NewReview review) {
        return service.addReview(id, review.rating(), review.description());
    }

    public record NewCourse(@NotBlank @Size(max = 100) String name) {
    }

    public record NewReview(@Min(1) @Max(5) int rating, @Size(max = 500) String description) {
    }
}
