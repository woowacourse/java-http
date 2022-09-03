package nextstep.jwp.handler;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.QueryParams;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    private LoginHandler() {
    }

    public static void login(QueryParams queryParams) {
        if (queryParams.contains(ACCOUNT) && queryParams.contains(PASSWORD)) {
            String account = queryParams.get(ACCOUNT);
            String password = queryParams.get(PASSWORD);

            Optional<User> user = InMemoryUserRepository.findByAccount(account);
            user.ifPresent(value -> loginSuccess(value, password));
        }
    }

    private static void loginSuccess(User user, String password) {
        if (user.checkPassword(password)) {
            log.info(user.toString());
        }
    }
}
