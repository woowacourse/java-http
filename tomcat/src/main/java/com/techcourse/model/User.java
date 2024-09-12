package com.techcourse.model;

import lombok.Getter;
import lombok.Setter;

@Getter
public class User {

    @Setter
    private Long id;
    private final String account;
    private final String password;
    private final String email;

    public User(Long id, String account, String password, String email) {
        validate(account, password, email);

        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    private void validate(String account, String password, String email) {
        if (account == null || account.isBlank()) {
            throw new IllegalArgumentException("account은 빈 문자열일 수 없습니다.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("password는 빈 문자열일 수 없습니다.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email은 빈 문자열일 수 없습니다.");
        }
    }

    public User(String account, String password, String email) {
        this(null, account, password, email);
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
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
