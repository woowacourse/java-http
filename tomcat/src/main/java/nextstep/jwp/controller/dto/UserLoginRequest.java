package nextstep.jwp.controller.dto;

import java.util.Map;

public class UserLoginRequest {

    private final String account;
    private final String password;

    public UserLoginRequest(final String account, final String password) {
        this.account = account;
        this.password = password;
    }

    public static UserLoginRequest from(final Map<String, String> value) {
        return new UserLoginRequest(value.get("account"), value.get("password"));
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }
}
