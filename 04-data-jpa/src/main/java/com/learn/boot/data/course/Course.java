package com.learn.boot.data.course;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.learn.boot.data.common.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

@Entity
public class Course extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    /** One course, many reviews. Review owns the foreign key (course_id). */
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    protected Course() {
    }

    public Course(String name) {
        this.name = name;
    }

    /** Helper keeps both sides of the relationship consistent. */
    public Review addReview(int rating, String description) {
        Review review = new Review(this, rating, description);
        reviews.add(review);
        return review;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Review> getReviews() { return Collections.unmodifiableList(reviews); }
}
