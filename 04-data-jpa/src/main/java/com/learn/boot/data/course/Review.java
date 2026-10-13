package com.learn.boot.data.course;

import com.learn.boot.data.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Review extends BaseEntity {

    @Column(nullable = false)
    private int rating;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id")
    private Course course;

    protected Review() {
    }

    Review(Course course, int rating, String description) {
        this.course = course;
        this.rating = rating;
        this.description = description;
    }

    public int getRating() { return rating; }
    public String getDescription() { return description; }
    public Course getCourse() { return course; }
}
