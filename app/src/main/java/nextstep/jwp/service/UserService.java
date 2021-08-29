package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.AuthorizationException;
import nextstep.jwp.exception.DuplicateAccountException;
import nextstep.jwp.exception.NoSuchUserException;
import nextstep.jwp.model.User;

import java.util.Map;

public class UserService {
    public void login(final String account, final String password) {
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(NoSuchUserException::new);

        if (!user.checkPassword(password)) {
            throw new AuthorizationException();
        }
    }

    public void save(final Map<String, String> payload) {
        final String account = payload.get("account");
        final String password = payload.get("password");
        final String email = payload.get("email");
        InMemoryUserRepository.findByAccount(account).ifPresent(user -> {
            throw new DuplicateAccountException();
        });

        final User user = new User(2L, account, password, email);
        InMemoryUserRepository.save(user);
    }
}
