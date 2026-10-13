package com.learn.boot.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.transaction.annotation.Transactional;

/** Full application with MockMvc (no real port). @Transactional also rolls back data created via the API. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApiTest {

    @Autowired
    MockMvcTester mvc;

    @Test
    void listCoursesWithStatsAndPaging() {
        assertThat(mvc.get().uri("/api/courses?size=1&sort=name,desc")).hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.content[0].name", v -> v.assertThat().isEqualTo("Spring Boot in Depth"))
                .hasPathSatisfying("$.content[0].averageRating", v -> v.assertThat().isEqualTo(4.5));
    }

    @Test
    void addReviewThenReadDetails() {
        assertThat(mvc.post().uri("/api/courses/3/reviews").contentType(MediaType.APPLICATION_JSON)
                .content("{\"rating\":5,\"description\":\"Clear\"}"))
                .hasStatus(HttpStatus.CREATED);
        assertThat(mvc.get().uri("/api/courses/3")).bodyJson().extractingPath("$.reviews[0].description").isEqualTo("Clear");
    }

    @Test
    void invalidReviewIs400AndUnknownCourseIs404() {
        assertThat(mvc.post().uri("/api/courses/1/reviews").contentType(MediaType.APPLICATION_JSON).content("{\"rating\":9}"))
                .hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(mvc.get().uri("/api/courses/999")).hasStatus(HttpStatus.NOT_FOUND)
                .bodyJson().extractingPath("$.detail").isEqualTo("Course 999 not found");
    }

    @Test
    void duplicateCourseNameIs409() {
        assertThat(mvc.post().uri("/api/courses").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Microservices\"}"))
                .hasStatus(HttpStatus.CONFLICT);
    }

    @Test
    void studentsEmployeesAndReports() {
        assertThat(mvc.put().uri("/api/students/2/courses/3")).hasStatusOk()
                .bodyJson().extractingPath("$.courses").asArray().contains("Microservices");
        assertThat(mvc.get().uri("/api/employees?partTimeOnly=true")).bodyJson()
                .extractingPath("$[0].type").isEqualTo("PART_TIME");
        assertThat(mvc.get().uri("/api/reports/top-courses?minReviews=2")).bodyJson()
                .extractingPath("$[0].course").isEqualTo("Spring Boot in Depth");
    }
}
