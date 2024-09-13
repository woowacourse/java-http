package com.techcourse.model;

public class User {

    private final Long id;
    private final UserAccount account;
    private final UserPassword password;
    private final UserEmail email;

    public User(Long id, String account, String password, String email) {
        this(id, new UserAccount(account), new UserPassword(password), new UserEmail(email));
    }

    public User(UserAccount account, UserPassword password, UserEmail email) {
        this(null, account, password, email);
    }

    private User(Long id, UserAccount account, UserPassword password, UserEmail email) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public boolean checkPassword(UserPassword password) {
        return this.password.equals(password);
    }

    public String getAccount() {
        return account.account();
    }

    public String getPassword() {
        return password.password();
    }

    public String getEmail() {
        return email.email();
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
