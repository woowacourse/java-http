package nextstep.jwp.handler.dto;

import nextstep.jwp.http.request.QueryParams;

public class LoginRequest {
    private final String account;
    private final String password;

    public LoginRequest(String account, String password) {
        this.account = account;
        this.password = password;
    }

    public static LoginRequest fromQueryParams(QueryParams params) {
        return new LoginRequest(params.get("account"), params.get("password"));
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }
}
