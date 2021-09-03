package nextstep.jwp.webserver.service;

import nextstep.jwp.webserver.db.InMemoryUserRepository;
import nextstep.jwp.webserver.model.User;

public class UserService {

    public void register(User user) {
        InMemoryUserRepository.save(user);
    }
}
