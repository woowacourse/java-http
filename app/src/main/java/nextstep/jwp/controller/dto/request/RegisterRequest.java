package nextstep.jwp.controller.dto.request;

import nextstep.jwp.model.User;

public class RegisterRequest {

    private final String account;
    private final String email;
    private final String password;

    public RegisterRequest(String account, String email, String password) {
        this.account = account;
        this.email = email;
        this.password = password;
    }

    public User toUser() {
        return new User(account, email, password);
    }
}
