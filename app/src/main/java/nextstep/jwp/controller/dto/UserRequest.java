package nextstep.jwp.controller.dto;

import nextstep.jwp.model.User;

public class UserRequest {

    private final String account;
    private final String password;
    private final String email;

    public UserRequest(String account, String password) {
        this(account, password, null);
    }

    public UserRequest(String account, String password, String email) {
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public User toEntity() {
        return new User(account, password, email);
    }
}
