package com.techcourse.dto;

public record LoginInfo(String account, String password) {

    public LoginInfo {
        validate(account, password);
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
