package com.techcourse.model;

public class User {

    private final Long id;
    private final String account;
    private final String password;
    private final String email;

    public User(Long id, String account, String password, String email) {
        validateAccount(account);
        validatePassword(password);
        validateEmail(email);

        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public User(String account, String password, String email) {
        this(null, account, password, email);
    }

    private void validateAccount(String account) {
        if (account == null || account.isBlank()) {
            throw new IllegalArgumentException("account는 비어 있을 수 없습니다.");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("password는 비어 있을 수 없습니다.");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email은 비어 있을 수 없습니다.");
        }
    }

    public boolean checkPassword(LoginCredentials loginCredentials) {
        return checkPassword(loginCredentials.getPassword());
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public String getAccount() {
        return account;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", account='" + account + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
