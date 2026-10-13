package com.learn.boot.rest.course;

public class CourseNotFoundException extends RuntimeException {
    public CourseNotFoundException(long id) {
        super("Course " + id + " not found");
    }
}
