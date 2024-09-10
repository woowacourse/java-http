package com.techcourse.dto;

import java.util.Map;

public record RegisterRequestDto(String account, String password, String email) {

    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";
    private static final String EMAIL_KEY = "email";

    public RegisterRequestDto {
        validate(account, password, email);
    }

    public static RegisterRequestDto of(Map<String, String> registerInfos) {
        return new RegisterRequestDto(
                registerInfos.get(ACCOUNT_KEY), registerInfos.get(PASSWORD_KEY), registerInfos.get(EMAIL_KEY));
    }

    private void validate(String account, String password, String email) {
        if (account == null || account.isEmpty()) {
            throw new IllegalArgumentException("Account cannot be empty");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
    }
}
