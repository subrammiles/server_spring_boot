package com.learn.boot.data.report;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportController {
    private final ReportRepository reports;

    public ReportController(ReportRepository reports) {
        this.reports = reports;
    }

    @GetMapping("/api/reports/top-courses")
    public List<ReportRepository.TopCourse> topCourses(@RequestParam(defaultValue = "1") int minReviews) {
        return reports.topRatedCourses(minReviews);
    }
}
