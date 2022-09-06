package nextstep.jwp.dto.request;

import java.util.Map;

public class LoginRequest {

    private final String account;
    private final String password;

    private LoginRequest(final Map<String, String> queryParams) {
        this.account = queryParams.get("account");
        this.password = queryParams.get("password");
    }

    public static LoginRequest from(final Map<String, String> queryParams) {
        return new LoginRequest(queryParams);
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }
}
