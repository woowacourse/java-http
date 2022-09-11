package nextstep.jwp.service;

import java.util.Map;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.AccountDuplicatedException;
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

        final Map<String, Object> sessionAttributes = Map.of("user", user);
        return SessionManager.generate(sessionAttributes);
    }

    public void register(final String account, final String password, final String email) {
        if (isUserPresent(account)) {
            throw new AccountDuplicatedException(account);
        }

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }

    private boolean isUserPresent(final String account) {
        return InMemoryUserRepository.findByAccount(account)
                .isPresent();
    }
}
