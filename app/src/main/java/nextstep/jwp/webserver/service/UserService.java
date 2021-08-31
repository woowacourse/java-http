package nextstep.jwp.webserver.service;

import java.util.Optional;

import nextstep.jwp.webserver.db.InMemoryUserRepository;
import nextstep.jwp.webserver.model.User;

public class UserService {

    public boolean login(User user) {
        final Optional<User> savedUser = InMemoryUserRepository.findByAccount(user.getAccount());

        if (savedUser.isEmpty()) {
            return false;
        }

        return savedUser.get().checkPassword(user);
    }

    public void register(User user) {
        InMemoryUserRepository.save(user);
    }
}
