package com.techcourse.model;

public class User {

    private final Long id;
    private final Account account;
    private final Password password;
    private final Email email;

    public User(final Long id, final Account account, final Password password, final Email email) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public User(final Account account, final Password password, final Email email) {
        this(null, account, password, email);
    }

    public boolean checkAccount(final Account account) {
        return this.account.equals(account);
    }

    public boolean checkPassword(final Password password) {
        return this.password.equals(password);
    }

    public Account getAccount() {
        return account;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", " + account +
                ", " + email +
                '}';
    }
}
