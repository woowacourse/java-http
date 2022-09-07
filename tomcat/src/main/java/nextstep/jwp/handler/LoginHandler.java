package nextstep.jwp.handler;

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

    public static boolean canLogin(QueryParams queryParams) {
        if (queryParams.contains(ACCOUNT) && queryParams.contains(PASSWORD)) {
            String account = queryParams.get(ACCOUNT);
            String password = queryParams.get(PASSWORD);
            return findUserByAccount(account, password);
        }
        return false;
    }

    private static boolean findUserByAccount(String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
            .filter(value -> isCorrectPassword(value, password))
            .isPresent();
    }

    private static boolean isCorrectPassword(User user, String password) {
        if (user.checkPassword(password)) {
            log.info(user.toString());
            return true;
        }
        return false;
    }
}
