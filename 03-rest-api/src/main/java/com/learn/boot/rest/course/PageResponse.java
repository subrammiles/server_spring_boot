package com.learn.boot.rest.course;

import java.util.List;
import java.util.function.Function;

/** A stable JSON shape for paginated lists. */
public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {

    public <R> PageResponse<R> map(Function<T, R> mapper) {
        return new PageResponse<>(content.stream().map(mapper).toList(), page, size, totalElements, totalPages);
    }
}
