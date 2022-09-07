package nextstep.jwp.service;

import java.util.Map;

public class RegisterRequest {

    private final String account;
    private final String email;
    private final String password;

    private RegisterRequest(final String account, final String email, final String password) {
        this.account = account;
        this.email = email;
        this.password = password;
    }

    public static RegisterRequest of(final Map<String, String> body) {
        return new RegisterRequest(body.get("account"), body.get("email"), body.get("password"));
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
