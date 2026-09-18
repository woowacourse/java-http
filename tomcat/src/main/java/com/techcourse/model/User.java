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

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public String getAccount() {
        return account;
    }

    public void isMatchPassword(String password) {
        if (!this.password.equals(password)) {
            throw new IllegalArgumentException("패스워드가 일치하지 않습니다.");
        }
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
