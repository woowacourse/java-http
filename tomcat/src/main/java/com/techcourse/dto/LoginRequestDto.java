package com.techcourse.dto;

import java.util.Map;

public record LoginRequestDto(String account, String password) {

    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";

    public LoginRequestDto {
        validate(account, password);
    }

    public static LoginRequestDto of(Map<String, String> loginInfos) {
        return new LoginRequestDto(loginInfos.get(ACCOUNT_KEY), loginInfos.get(PASSWORD_KEY));
    }

    private void validate(String account, String password) {
        if (account == null || account.isEmpty()) {
            throw new IllegalArgumentException("Account cannot be empty");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
    }
}
