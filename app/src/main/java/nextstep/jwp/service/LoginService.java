package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    public void loginValidate(User user) {
        User expectedUser = InMemoryUserRepository
            .findByAccount(user.getAccount())
            .orElseThrow(UnauthorizedException::new);

        if (expectedUser.checkPassword(user.getPassword())) {
            log.info("{} login success", user.getAccount());
            return;
        }
        throw new UnauthorizedException();
    }
}
