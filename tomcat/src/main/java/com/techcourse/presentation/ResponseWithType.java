package com.techcourse.presentation;

public record ResponseWithType(
        String contentType,
        String body
) {

}
