package nextstep.jwp.handler.dto;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.QueryParams;
import nextstep.jwp.model.User;

public class RegisterRequest {
    private final String account;
    private final String password;
    private final String email;

    public RegisterRequest(String account, String password, String email) {
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public static RegisterRequest fromQueryParams(QueryParams params) {
        return new RegisterRequest(params.get("account"), params.get("password"), params.get("email"));
    }

    public static RegisterRequest fromHttpRequest(HttpRequest request) {
        QueryParams params = QueryParams.of(request.requestBody());
        return fromQueryParams(params);
    }

    public User toEntity() {
        return new User(account, password, email);
    }
}
