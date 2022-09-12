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
        final User user = findUserOrThrow(account);
        validatePasswordMatches(user, password);

        final Map<String, Object> sessionAttributes = Map.of("user", user);
        return SessionManager.generate(sessionAttributes);
    }

    private User findUserOrThrow(final String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(UserNotFoundException::new);
    }

    private void validatePasswordMatches(final User user, final String password) {
        if (!user.checkPassword(password)) {
            throw new LoginFailedException();
        }
    }

    public void register(final String account, final String password, final String email) {
        validateAccountNotDuplicated(account);

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }

    private void validateAccountNotDuplicated(final String account) {
        if (existAccount(account)) {
            throw new AccountDuplicatedException(account);
        }
    }

    private boolean existAccount(final String account) {
        return InMemoryUserRepository.findByAccount(account)
                .isPresent();
    }
}
