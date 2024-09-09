package com.techcourse.dto;

public record RegisterInfo(String account, String password, String email) {

    public RegisterInfo {
        validate(account, password, email);
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
