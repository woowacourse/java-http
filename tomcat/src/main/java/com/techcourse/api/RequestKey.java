package com.techcourse.api;

public record RequestKey(
        String method,
        String path
) {
}
