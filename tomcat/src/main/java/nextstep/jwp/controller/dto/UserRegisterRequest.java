package nextstep.jwp.controller.dto;

import java.util.Map;
import nextstep.jwp.model.User;

public class UserRegisterRequest {

    private final String account;
    private final String password;
    private final String email;

    public UserRegisterRequest(final String account, final String password, final String email) {
        this.account = account;
        this.password = password;
        this.email = email;
    }

    public static UserRegisterRequest from(final Map<String, String> value) {
        return new UserRegisterRequest(value.get("account"), value.get("password"), value.get("email"));
    }

    public User toDomain() {
        return new User(account, password, email);
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
}
