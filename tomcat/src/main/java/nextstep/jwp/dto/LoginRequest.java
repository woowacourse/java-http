package nextstep.jwp.dto;

import java.util.Map;

public class LoginRequest {

    private final String account;
    private final String password;

    public LoginRequest(final String account, final String password) {
        this.account = convertAccount(account);
        this.password = convertPassword(password);
    }

    public static LoginRequest of(final Map<String, String> mapping) {
        return new LoginRequest(mapping.get("account"), mapping.get("password"));
    }

    private String convertAccount(final String account) {
        if (account == null) {
            return "";
        }
        return account;
    }

    private String convertPassword(final String password) {
        if (password == null) {
            return "";
        }
        return password;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }
}
