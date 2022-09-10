package nextstep.jwp.dto;

import nextstep.jwp.exception.NotEnoughConditionException;

import java.util.Map;

public class LoginRequest {

    private final String account;
    private final String password;

    public LoginRequest(final String account, final String password) {
        this.account = validateAccount(account);
        this.password = validatePassword(password);
    }

    public static LoginRequest of(final Map<String, String> mapping) {
        return new LoginRequest(mapping.get("account"), mapping.get("password"));
    }

    private String validateAccount(final String account) {
        if (account == null) {
            throw new NotEnoughConditionException();
        }
        return account;
    }

    private String validatePassword(final String password) {
        if (password == null) {
            throw new NotEnoughConditionException();
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
