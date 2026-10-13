package com.learn.boot.rest.course;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

/** In-memory storage keeps this module about HTTP. Module 04 replaces it with a database. */
@Service
public class CourseService {
    private final Map<Long, Course> courses = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong();

    public CourseService() {
        create(new CourseRequest("Spring Boot Fundamentals", "backend", 12, new BigDecimal("1999.00")));
        create(new CourseRequest("Data Structures and Algorithms", "cs", 40, new BigDecimal("2999.00")));
        create(new CourseRequest("REST API Design", "backend", 8, new BigDecimal("999.00")));
    }

    public PageResponse<Course> search(String category, String query, int page, int size, String sortBy) {
        Comparator<Course> order = switch (sortBy) {
            case "price" -> Comparator.comparing(Course::price);
            case "duration" -> Comparator.comparingInt(Course::durationHours);
            default -> Comparator.comparing(Course::title);
        };
        List<Course> matches = courses.values().stream()
                .filter(c -> category == null || c.category().equalsIgnoreCase(category))
                .filter(c -> query == null || c.title().toLowerCase().contains(query.toLowerCase()))
                .sorted(order)
                .toList();
        List<Course> pageContent = matches.stream().skip((long) page * size).limit(size).toList();
        int totalPages = (int) Math.ceil(matches.size() / (double) size);
        return new PageResponse<>(pageContent, page, size, matches.size(), totalPages);
    }

    public Course get(long id) {
        Course course = courses.get(id);
        if (course == null) {
            throw new CourseNotFoundException(id);
        }
        return course;
    }

    public Course create(CourseRequest request) {
        boolean duplicate = courses.values().stream().anyMatch(c -> c.title().equalsIgnoreCase(request.title()));
        if (duplicate) {
            throw new DuplicateCourseException(request.title());
        }
        long id = ids.incrementAndGet();
        Course course = new Course(id, request.title(), request.category(), request.durationHours(), request.price(), Instant.now());
        courses.put(id, course);
        return course;
    }

    public Course replace(long id, CourseRequest request) {
        Course updated = get(id).withChanges(request.title(), request.category(), request.durationHours(), request.price());
        courses.put(id, updated);
        return updated;
    }

    public Course patch(long id, CoursePatch patch) {
        Course current = get(id);
        Course updated = current.withChanges(
                patch.title() != null ? patch.title() : current.title(),
                current.category(),
                current.durationHours(),
                patch.price() != null ? patch.price() : current.price());
        courses.put(id, updated);
        return updated;
    }

    public void delete(long id) {
        if (courses.remove(id) == null) {
            throw new CourseNotFoundException(id);
        }
    }
}
