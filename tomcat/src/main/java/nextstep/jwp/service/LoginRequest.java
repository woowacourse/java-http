package nextstep.jwp.service;

import java.util.Map;

public class LoginRequest {

    private final String account;
    private final String password;

    public LoginRequest(final String account, final String password) {
        this.account = account;
        this.password = password;
    }

    public static LoginRequest of(final Map<String, String> body) {
        return new LoginRequest(body.get("account"), body.get("password"));
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }
}
