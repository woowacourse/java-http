package com.techcourse.model;

public record StaticResource(
        String contentType,
        byte[] body
) {
}
