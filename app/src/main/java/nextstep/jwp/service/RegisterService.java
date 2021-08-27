package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class RegisterService {

    public void register(User user) {
        InMemoryUserRepository.save(user);
    }
}
