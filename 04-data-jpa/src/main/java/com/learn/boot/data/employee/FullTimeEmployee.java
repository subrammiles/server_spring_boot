package com.learn.boot.data.employee;

import java.math.BigDecimal;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/** The original version forgot @Entity here, so saving one failed with "Unknown entity". */
@Entity
@DiscriminatorValue("FULL_TIME")
public class FullTimeEmployee extends Employee {

    private BigDecimal salary;

    protected FullTimeEmployee() {
    }

    public FullTimeEmployee(String name, BigDecimal salary) {
        super(name);
        this.salary = salary;
    }

    public BigDecimal getSalary() { return salary; }

    @Override
    public String type() { return "FULL_TIME"; }
}
