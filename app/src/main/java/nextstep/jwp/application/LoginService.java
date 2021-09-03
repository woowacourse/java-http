package nextstep.jwp.application;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.InvalidUserException;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    public User login(String account, String password) {
        User loginUser = InMemoryUserRepository.findByAccount(account)
            .orElseThrow(InvalidUserException::new);

        if (!loginUser.checkPassword(password)) {
            throw new InvalidUserException();
        }

        String userInfo = loginUser.toString();
        logger.debug("Login User !!! - {}", userInfo);

        return loginUser;
    }
}
