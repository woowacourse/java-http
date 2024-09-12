package com.techcourse.param;

import org.apache.coyote.request.HttpRequest;

public class LoginParam {

    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";

    private final String account;
    private final String password;

    public LoginParam(HttpRequest httpRequest) {
        this.account = httpRequest.getValueFromBody(ACCOUNT_KEY);
        this.password = httpRequest.getValueFromBody(PASSWORD_KEY);
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }
}
