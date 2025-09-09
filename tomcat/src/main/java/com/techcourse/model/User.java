package com.techcourse.model;

import java.util.concurrent.atomic.AtomicLong;

public class User {
    private static final AtomicLong lastId = new AtomicLong(0);

    private final Long id;
    private final String account;
    private final String password;
    private final String email;

    public User(String account, String password, String email) {
        this.id = lastId.incrementAndGet();
        this.account = account;
        this.password = password;
        this.email = email;
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
