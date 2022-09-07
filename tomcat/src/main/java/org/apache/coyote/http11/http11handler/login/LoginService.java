package org.apache.coyote.http11.http11handler.login;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.user.User;
import nextstep.jwp.model.user.exception.UserNotFoundException;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    public boolean login(String account, String password) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty() || !user.get().checkPassword(password)) {
            return false;
        }
        log.info(String.valueOf(user.get()));
        return true;
    }

    public User findUser(String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(UserNotFoundException::new);
    }

    public boolean isExistUser(User user) {
        return InMemoryUserRepository.findByAccount(user.getAccount()).isPresent();
    }
}
