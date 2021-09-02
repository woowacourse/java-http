package nextstep.jwp.application;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;

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
