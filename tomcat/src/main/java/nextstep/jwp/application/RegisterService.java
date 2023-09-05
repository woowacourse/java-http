package nextstep.jwp.application;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class RegisterService {

    public void register(final String account, final String password, final String email) {
        InMemoryUserRepository.save(new User(account, password, email));
    }
}
