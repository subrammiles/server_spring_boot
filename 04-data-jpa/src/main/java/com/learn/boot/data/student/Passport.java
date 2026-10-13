package com.learn.boot.data.student;

import com.learn.boot.data.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

@Entity
public class Passport extends BaseEntity {

    @Column(nullable = false, unique = true, length = 20)
    private String number;

    /** Inverse side of the one-to-one: Student owns the passport_id column. */
    @OneToOne(mappedBy = "passport")
    private Student student;

    protected Passport() {
    }

    public Passport(String number) {
        this.number = number;
    }

    public String getNumber() { return number; }
    public Student getStudent() { return student; }
    void setStudent(Student student) { this.student = student; }
}
