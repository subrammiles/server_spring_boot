package com.learn.boot.data.employee;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /** findAll() is POLYMORPHIC: it returns FullTimeEmployee and PartTimeEmployee objects. */
    List<Employee> findAllByOrderByName();

    /** TYPE() filters on the subclass. */
    @Query("SELECT e FROM Employee e WHERE TYPE(e) = PartTimeEmployee ORDER BY e.name")
    List<Employee> findPartTimers();
}
