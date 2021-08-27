package nextstep.jwp.controller.dto.request;

import nextstep.jwp.model.User;

public class RegisterRequest {

    private final String account;
    private final String password;
    private final String email;

    public RegisterRequest(String account, String password, String email) {
        this.email = email;
        this.account = account;
        this.password = password;
    }

    public User toUser() {
        return new User(account, password, email);
    }
}
