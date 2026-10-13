package com.learn.boot.rest.error;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.learn.boot.rest.course.CourseNotFoundException;
import com.learn.boot.rest.course.DuplicateCourseException;

/**
 * ONE place that turns exceptions into consistent RFC 9457 "problem+json" responses.
 * Extending ResponseEntityExceptionHandler covers all built-in Spring MVC errors
 * (bad JSON, wrong method, unsupported media type, ...). We add our own and enrich validation errors.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CourseNotFoundException.class)
    public ProblemDetail notFound(CourseNotFoundException e) {
        return problem(HttpStatus.NOT_FOUND, "Course not found", e.getMessage());
    }

    @ExceptionHandler(DuplicateCourseException.class)
    public ProblemDetail conflict(DuplicateCourseException e) {
        return problem(HttpStatus.CONFLICT, "Duplicate course", e.getMessage());
    }

    /** @Valid @RequestBody failures: list every invalid field. */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new TreeMap<>();
        e.getBindingResult().getFieldErrors().forEach(f -> errors.put(f.getField(), f.getDefaultMessage()));
        ProblemDetail body = problem(HttpStatus.BAD_REQUEST, "Validation failed", "Request body has invalid fields");
        body.setProperty("errors", errors);
        return handleExceptionInternal(e, body, headers, status, request);
    }

    /** @Min/@Max on @RequestParam / @PathVariable failures. */
    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException e,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new TreeMap<>();
        e.getParameterValidationResults().forEach(r -> r.getResolvableErrors().forEach(err ->
                errors.put(r.getMethodParameter().getParameterName(), err.getDefaultMessage())));
        ProblemDetail body = problem(HttpStatus.BAD_REQUEST, "Validation failed", "Request parameters are invalid");
        body.setProperty("errors", errors);
        return handleExceptionInternal(e, body, headers, status, request);
    }

    private static ProblemDetail problem(HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        return problem;
    }
}
