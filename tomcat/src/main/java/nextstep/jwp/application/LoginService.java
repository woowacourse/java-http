package nextstep.jwp.application;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class LoginService {

    public User findUser(String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(IllegalArgumentException::new);
    }
}
