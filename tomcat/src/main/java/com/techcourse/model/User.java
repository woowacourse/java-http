package com.techcourse.model;

import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class User {

    @Nullable
    private final Long id;
    private final String account;
    private final String password;
    private final String email;

    public User(final Long id, final String account, final String password, final String email) {
        Objects.requireNonNull(account, "account must not be null");
        Objects.requireNonNull(password, "password must not be null");
        Objects.requireNonNull(email, "email must not be null");
        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public static User of(final String account, final String password, final String email) {
        return new User(null, account, password, email);
    }

    public boolean checkPassword(final String password) {
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
