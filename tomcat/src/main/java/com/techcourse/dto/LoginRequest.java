package com.techcourse.dto;

public record LoginRequest(
    String account,
    String password
) {
}
