package com.techcourse.resource;

public record StaticResource(
        String contentType,
        byte[] body
) {
}
