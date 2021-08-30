package nextstep.jwp.controller.request;

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

    public User toEntity() {
        return new User(account, password, email);
    }
}
