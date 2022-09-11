package nextstep.jwp.service;

import java.util.Map;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.LoginFailedException;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;

public class AuthService {

    public Session login(final String account, final String password) {
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(UserNotFoundException::new);

        if (!user.checkPassword(password)) {
            throw new LoginFailedException();
        }

        return SessionManager.generate(Map.of("user", user));
    }
}
