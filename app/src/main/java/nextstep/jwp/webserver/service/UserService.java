package nextstep.jwp.webserver.service;

import java.util.Optional;

import nextstep.jwp.webserver.db.InMemoryUserRepository;
import nextstep.jwp.webserver.model.User;

public class UserService {

    public boolean login(String account, String password) {
        final Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isEmpty()) {
            return false;
        }

        return user.get().checkPassword(password);
    }
}
