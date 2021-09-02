package nextstep.jwp.web.application;

import nextstep.jwp.server.exception.UnauthorizedException;
import nextstep.jwp.web.db.InMemoryUserRepository;
import nextstep.jwp.web.exception.UserNotFoundException;
import nextstep.jwp.web.model.User;

public class LoginService {

    public User login(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(UserNotFoundException::new);
        if (!user.checkPassword(password)) {
            throw new UnauthorizedException();
        }
        return user;
    }
}
