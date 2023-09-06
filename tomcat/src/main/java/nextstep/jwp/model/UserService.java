package nextstep.jwp.model;

import java.util.Map;

public class UserService {
    private UserService() {
    }

    public static User parseToUser(final Map<String, String> bodies) {
        final String account = bodies.get("account");
        final String password = bodies.get("password");
        final String mail = bodies.get("email");
        return new User(account, password, mail);
    }
}
