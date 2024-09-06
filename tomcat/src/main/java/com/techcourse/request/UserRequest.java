package com.techcourse.request;

public class UserRequest {
    private final String account;
    private final String password;

    public UserRequest(String account, String password) {
        this.account = account;
        this.password = password;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }
}
