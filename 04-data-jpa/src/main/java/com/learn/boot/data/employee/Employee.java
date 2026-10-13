package com.learn.boot.data.employee;

import com.learn.boot.data.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

/**
 * INHERITANCE MAPPING. SINGLE_TABLE: all subclasses share one "employee" table, and the
 * employee_type column says which subclass a row is. Fast (no joins) but subclass columns must be nullable.
 * Alternatives: JOINED (table per subclass, joined on read) and TABLE_PER_CLASS.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "employee_type")
public abstract class Employee extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    protected Employee() {
    }

    protected Employee(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    public abstract String type();
}
