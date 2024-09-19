package org.apache.coyote.http11.request;

public class LoginRequest {

    private final String account;
    private final String password;

    private LoginRequest(final String account, final String password) {
        this.account = account;
        this.password = password;
    }

    public static LoginRequest from(final String body) {
        final String[] loginRequestPair = body.split("&");
        final String account = loginRequestPair[0].split("=")[1];
        final String password = loginRequestPair[1].split("=")[1];
        return new LoginRequest(account, password);
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }
}
