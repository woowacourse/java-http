package com.techcourse.model;

public class LoginCredentials {

    private final String account;
    private final String password;

    public LoginCredentials(String account, String password) {
        validateAccount(account);
        validatePassword(password);
        this.account = account;
        this.password = password;
    }

    private void validateAccount(String account) {
        if (account == null || account.isEmpty()) {
            throw new IllegalArgumentException("account는 비어 있을 수 없습니다.");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("password는 비어 있을 수 없습니다.");
        }
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }
}
