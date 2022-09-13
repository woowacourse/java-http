package nextstep.jwp.dto.request;

import java.util.Map;
import nextstep.jwp.model.User;

public class RegisterRequest {


    private final String account;
    private final String password;
    private final String email;

    private RegisterRequest(final Map<String, String> params) {
        this.account = params.get("account");
        this.password = params.get("password");
        this.email = params.get("email");
    }

    public static RegisterRequest from(final Map<String, String> params) {
        return new RegisterRequest(params);
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

    public User toUser() {
        return new User(account, password, email);
    }
}
