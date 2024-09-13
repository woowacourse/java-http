package com.techcourse.param;

import org.apache.coyote.request.HttpRequest;

public class RegisterParam {

    private static final String ACCOUNT_KEY = "account";
    private static final String EMAIL_KEY = "email";
    private static final String PASSWORD_KEY = "password";

    private final String account;
    private final String email;
    private final String password;

    public RegisterParam(HttpRequest httpRequest) {
        this.account = httpRequest.getValueFromBody(ACCOUNT_KEY);
        this.email = httpRequest.getValueFromBody(EMAIL_KEY);
        this.password = httpRequest.getValueFromBody(PASSWORD_KEY);
    }

    public String getAccount() {
        return account;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
