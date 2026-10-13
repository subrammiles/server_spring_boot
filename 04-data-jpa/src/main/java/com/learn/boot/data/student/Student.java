package com.learn.boot.data.student;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.learn.boot.data.common.BaseEntity;
import com.learn.boot.data.course.Course;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Student extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    /** One-to-one, owning side. LAZY: the original code used EAGER, which loads it even when unused. */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "passport_id", unique = true)
    private Passport passport;

    /** Many-to-many through the join table student_course. */
    @ManyToMany
    @JoinTable(name = "student_course",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    private Set<Course> courses = new HashSet<>();

    protected Student() {
    }

    public Student(String name, Passport passport) {
        this.name = name;
        this.passport = passport;
        passport.setStudent(this);
    }

    /** Returns false if already enrolled (a Set ignores duplicates). */
    public boolean enroll(Course course) {
        return courses.add(course);
    }

    public String getName() { return name; }
    public Passport getPassport() { return passport; }
    public Set<Course> getCourses() { return Collections.unmodifiableSet(courses); }
}
