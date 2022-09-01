package nextstep.jwp.handler;

import nextstep.Application;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public void login(String account, String password) {
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new UserNotFoundException(account));

        if (user.checkPassword(password)) {
            log.info(user.toString());
        }
    }
}
