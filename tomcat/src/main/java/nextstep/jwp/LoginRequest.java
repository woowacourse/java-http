package nextstep.jwp;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class LoginRequest {

    private final String account;
    private final String password;

    public LoginRequest(final String account, final String password) {
        this.account = account;
        this.password = password;
    }

    public static LoginRequest of(final String body) {
        Map<String, String> fields = Arrays.stream(body.split("&"))
                .map(it -> it.split("="))
                .collect(Collectors.toMap(it -> it[0], it -> it[1]));
        return new LoginRequest(fields.get("account"),fields.get("password"));
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }
}
