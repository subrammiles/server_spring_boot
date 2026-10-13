package com.learn.boot.data.employee;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Ported from the old InheritanceRESTController, now with DTOs and working subclasses. */
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeRepository employees;

    public EmployeeController(EmployeeRepository employees) {
        this.employees = employees;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<EmployeeDto> all(@RequestParam(defaultValue = "false") boolean partTimeOnly) {
        var list = partTimeOnly ? employees.findPartTimers() : employees.findAllByOrderByName();
        return list.stream().map(EmployeeDto::from).toList();
    }

    @PostMapping("/part-time")
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeDto hirePartTimer(@RequestParam String name, @RequestParam BigDecimal hourlyWage) {
        return EmployeeDto.from(employees.save(new PartTimeEmployee(name, hourlyWage)));
    }

    public record EmployeeDto(Long id, String name, String type, BigDecimal pay) {
        static EmployeeDto from(Employee e) {
            BigDecimal pay = switch (e) {
                case FullTimeEmployee f -> f.getSalary();
                case PartTimeEmployee p -> p.getHourlyWage();
                default -> null;
            };
            return new EmployeeDto(e.getId(), e.getName(), e.type(), pay);
        }
    }
}
