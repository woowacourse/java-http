package com.techcourse.model.dto;

public record LoginRequest(
        String account,
        String password
) {
}
