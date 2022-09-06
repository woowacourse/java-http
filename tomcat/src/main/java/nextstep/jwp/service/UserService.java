package nextstep.jwp.service;

import java.util.Optional;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class UserService {

    private UserService() {
    }

    public static Optional<User> findUser(final String account, final String password) {
        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty()) {
            return user;
        }
        if (!user.get().checkPassword(password)) {
            return Optional.empty();
        }
        return user;
    }
}
