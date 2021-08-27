package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class RegisterService {

    private final InMemoryUserRepository inMemoryUserRepository;

    public RegisterService(InMemoryUserRepository inMemoryUserRepository) {
        this.inMemoryUserRepository = inMemoryUserRepository;
    }

    public void register(User user) {
        inMemoryUserRepository.save(user);
    }
}
