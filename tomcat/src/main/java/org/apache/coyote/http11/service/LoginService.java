package org.apache.coyote.http11.service;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    public void checkUser(String account, String password) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent()) {
            checkPassword(user.get(), password);
        }
    }

    private void checkPassword(User user, String password) {
        if (user.checkPassword(password)) {
            log.info("{}", user);
        }
    }
}
