package com.learn.boot.rest.course;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

/**
 * SLICE TEST: @WebMvcTest starts ONLY the web layer (controllers, advice, JSON, validation).
 * Services are not created, so we mock them. Fast and focused on HTTP behaviour.
 */
@WebMvcTest(CourseController.class)
class CourseControllerWebMvcTest {

    @Autowired
    MockMvcTester mvc;

    @MockitoBean
    CourseService service;

    private static final Course SPRING = new Course(7, "Spring", "backend", 10, new BigDecimal("100.00"), Instant.EPOCH);

    @Test
    void getReturnsVersion1ByDefault() {
        given(service.get(7)).willReturn(SPRING);

        assertThat(mvc.get().uri("/api/courses/7")).hasStatusOk()
                .bodyJson().extractingPath("$.price").isEqualTo(100.00);
    }

    @Test
    void version2IsSelectedByHeader() {
        given(service.get(7)).willReturn(SPRING);

        assertThat(mvc.get().uri("/api/courses/7").header("API-Version", "2.0")).hasStatusOk()
                .bodyJson().extractingPath("$.price.amountWithGst").isEqualTo(118.00);
    }

    @Test
    void createReturns201WithLocation() {
        given(service.create(any())).willReturn(SPRING);

        assertThat(mvc.post().uri("/api/courses").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title":"Spring","category":"backend","durationHours":10,"price":100.00}"""))
                .hasStatus(HttpStatus.CREATED)
                .hasHeader("Location", "http://localhost/api/courses/7");
    }

    @Test
    void invalidBodyIs400WithFieldErrorsAndServiceIsNeverCalled() {
        assertThat(mvc.post().uri("/api/courses").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title":"","category":"backend","durationHours":-1}"""))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson().extractingPath("$.errors").asMap()
                .containsOnlyKeys("title", "durationHours", "price");
        verifyNoInteractions(service);
    }

    @Test
    void invalidQueryParameterIs400() {
        assertThat(mvc.get().uri("/api/courses?size=1000")).hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson().extractingPath("$.errors.size").isNotNull();
    }

    @Test
    void domainExceptionsBecomeProblemDetails() {
        given(service.get(99)).willThrow(new CourseNotFoundException(99));

        assertThat(mvc.get().uri("/api/courses/99")).hasStatus(HttpStatus.NOT_FOUND)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON)
                .bodyJson().extractingPath("$.detail").isEqualTo("Course 99 not found");
    }

    @Test
    void malformedJsonIsAlsoAProblemDetail() {
        assertThat(mvc.post().uri("/api/courses").contentType(MediaType.APPLICATION_JSON).content("{oops"))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON);
    }
}
