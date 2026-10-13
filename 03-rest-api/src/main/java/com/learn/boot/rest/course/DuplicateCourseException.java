package com.learn.boot.rest.course;

public class DuplicateCourseException extends RuntimeException {
    public DuplicateCourseException(String title) {
        super("A course titled '" + title + "' already exists");
    }
}
