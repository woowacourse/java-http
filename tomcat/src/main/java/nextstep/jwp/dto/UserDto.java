package nextstep.jwp.dto;

import nextstep.jwp.model.User;

public class UserDto {

    private final String account;
    private final String password;
    private final String email;

    public UserDto(final String account, final String password, final String email) {
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public User toUser() {
        return new User(account, password, email);
    }
}
