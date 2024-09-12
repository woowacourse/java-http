package com.techcourse.model;

import java.util.Objects;

public class User {

    private final Long id;
    private final String account;
    private final String password;
    private final String email;

    public User(Long id, String account, String password, String email) {
        validateNonNull(account, password, email);
        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    private void validateNonNull(String account, String password, String email) {
        if (isNullOrEmpty(account) || isNullOrEmpty(password) || isNullOrEmpty(email)) {
            throw new IllegalArgumentException("사용자 필수 정보가 누락되었습니다.");
        }
    }

    private boolean isNullOrEmpty(String value) {
        return Objects.isNull(value) || value.trim().isEmpty();
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
