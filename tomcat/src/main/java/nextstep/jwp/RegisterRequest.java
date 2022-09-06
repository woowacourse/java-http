package nextstep.jwp;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class RegisterRequest {

    private final String account;
    private final String email;
    private final String password;

    private RegisterRequest(final String account, final String email, final String password) {
        this.account = account;
        this.email = email;
        this.password = password;
    }

    public static RegisterRequest of(final String body) {
        Map<String, String> fields = Arrays.stream(body.split("&"))
                .map(it -> it.split("="))
                .collect(Collectors.toMap(it -> it[0], it -> it[1]));
        return new RegisterRequest(fields.get("account"), fields.get("email"), fields.get("password"));
    }

    public String getAccount() {
        return account;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
