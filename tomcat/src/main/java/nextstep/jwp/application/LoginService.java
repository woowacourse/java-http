package nextstep.jwp.application;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NotFoundUserException;
import nextstep.jwp.model.User;

public class LoginService {

    public User findUser(final String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(NotFoundUserException::new);
    }

    public boolean existsUser(final User user) {
        return InMemoryUserRepository.findByAccount(user.getAccount())
                .isPresent();
    }
}
