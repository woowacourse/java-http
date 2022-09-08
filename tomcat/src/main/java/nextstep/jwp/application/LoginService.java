package nextstep.jwp.application;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class LoginService {

    public Optional<User> findUser(String account) {
        return InMemoryUserRepository.findByAccount(account);
    }
}
