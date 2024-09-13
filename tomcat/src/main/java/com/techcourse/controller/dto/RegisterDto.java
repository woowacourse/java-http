package com.techcourse.controller.dto;

import org.apache.coyote.http11.request.HttpRequest;

public record RegisterDto(
        String account,
        String password,
        String email
) {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    public static RegisterDto of(HttpRequest request) {
        String account = request.getBodyParameter(ACCOUNT);
        String password = request.getBodyParameter(PASSWORD);
        String email = request.getBodyParameter(EMAIL);

        return new RegisterDto(account, password, email);
    }
}
