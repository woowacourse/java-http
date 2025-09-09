package com.techcourse.model;

public class User {

    private final Long id;
    private final String account;
    private final String password;
    private final String email;

    public User(Long id, String account, String password, String email) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public User(String account, String password, String email) {
        this(null, account, password, email);
    }

    public boolean hasSameId(Long id) {
        if (this.id == null || id == null) {
            return false;
        }
        return this.id.equals(id);
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public long getId() {
        return id;
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
