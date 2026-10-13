package com.learn.boot.data.course;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.learn.boot.data.common.NotFoundException;

/**
 * The service layer owns the TRANSACTIONS and converts entities to DTOs.
 * Controllers never see entities, so lazy-loading problems cannot leak into JSON serialization.
 */
@Service
@Transactional(readOnly = true)
public class CourseService {
    private final CourseRepository courses;

    public CourseService(CourseRepository courses) {
        this.courses = courses;
    }

    public Page<CourseStats> stats(Pageable pageable) {
        return courses.findStats(pageable);
    }

    public CourseDetails details(long id) {
        Course course = courses.findWithReviewsById(id).orElseThrow(() -> new NotFoundException("Course", id));
        return CourseDetails.from(course);
    }

    @Transactional
    public CourseDetails create(String name) {
        return CourseDetails.from(courses.save(new Course(name)));
    }

    /** No save() call: the course is managed, so the new review is cascaded on commit (dirty checking). */
    @Transactional
    public CourseDetails addReview(long courseId, int rating, String description) {
        Course course = courses.findWithReviewsById(courseId).orElseThrow(() -> new NotFoundException("Course", courseId));
        course.addReview(rating, description);
        return CourseDetails.from(course);
    }

    public record CourseDetails(Long id, String name, List<ReviewDto> reviews) {
        static CourseDetails from(Course c) {
            return new CourseDetails(c.getId(), c.getName(),
                    c.getReviews().stream().map(r -> new ReviewDto(r.getRating(), r.getDescription())).toList());
        }
    }

    public record ReviewDto(int rating, String description) {
    }
}
