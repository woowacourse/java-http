package com.techcourse.model;

import static java.util.Objects.requireNonNull;

public class User {

    private final Long id;
    private final String account;
    private final String password;
    private final String email;

    public User(Long id, String account, String password, String email) {
        this.id = id;
        this.account = checkEmpty(account, "계정이 비어있다.");
        this.password = checkEmpty(password, "비밀번호가 비어있다.");
        this.email = checkEmpty(email, "이메일이 비어있다.");
    }

    private static String checkEmpty(String data, String message) {
        if (requireNonNull(data, message).isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return data;
    }

    public User(String account, String password, String email) {
        this(null, account, password, email);
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
