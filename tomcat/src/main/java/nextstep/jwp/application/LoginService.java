package nextstep.jwp.application;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    public static void checkAccount(Map<String, String> queryParams) {
        final Optional<User> foundUser = InMemoryUserRepository.findByAccount(queryParams.get("account"));
        if (foundUser.isPresent()) {
            User user = foundUser.get();
            if (hasSamePassword(user, queryParams.get("password"))) {
                log.info("user = {}", user);
                return;
            }
        }
        log.info("unauhthorized request");
    }

    private static boolean hasSamePassword(User user, String password) {
        return user.checkPassword(password);
    }
}
