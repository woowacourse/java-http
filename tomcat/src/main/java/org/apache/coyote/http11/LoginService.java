package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    public void validateAccount(String account, String password) {
        User existedUser = findUser(account, password);
        log.info(existedUser.toString());
    }

    private User findUser(String account, String password) {
        User existedUser = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(LoginFailureException::new);
        if (!existedUser.checkPassword(password)) {
            throw new LoginFailureException();
        }
        return existedUser;
    }
}
