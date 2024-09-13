package com.techcourse.controller.dto;

import org.apache.coyote.http11.request.HttpRequest;

public record LoginRequestDto(
        String account,
        String password
) {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    public static LoginRequestDto of(HttpRequest request) {
        String account = request.getBodyParameter(ACCOUNT);
        String password = request.getBodyParameter(PASSWORD);

        return new LoginRequestDto(account, password);
    }
}
