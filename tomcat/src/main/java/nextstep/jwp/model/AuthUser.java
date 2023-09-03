package nextstep.jwp.model;

import java.util.Map;

public class AuthUser {
    private final String account;
    private final String password;

    public AuthUser(String account, String password) {
        this.account = account;
        this.password = password;
    }

    public static AuthUser from(Map<String, String> query){
        return new AuthUser(query.get("account"),query.get("password"));
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }
}
