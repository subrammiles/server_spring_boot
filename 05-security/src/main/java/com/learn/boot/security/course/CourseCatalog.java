package com.learn.boot.security.course;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * METHOD SECURITY: rules on the service itself, a second line of defence behind the URL rules.
 * It also protects calls that don't come through HTTP (scheduled jobs, message listeners...).
 */
@Service
public class CourseCatalog {
    private final Map<Long, String> courses = new ConcurrentSkipListMap<>(Map.of(1L, "Spring Security", 2L, "OAuth 2 and JWT"));
    private final AtomicLong ids = new AtomicLong(2);

    public List<Map<String, Object>> all() {
        return courses.entrySet().stream().map(e -> Map.<String, Object>of("id", e.getKey(), "title", e.getValue())).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public long add(String title) {
        long id = ids.incrementAndGet();
        courses.put(id, title);
        return id;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public boolean remove(long id) {
        return courses.remove(id) != null;
    }

    /** Expression-based rule: users may only read their OWN enrolments, admins anyone's. */
    @PreAuthorize("#username == authentication.name or hasRole('ADMIN')")
    public List<String> enrolmentsOf(String username) {
        return List.of(username + " is enrolled in: Spring Security");
    }
}
