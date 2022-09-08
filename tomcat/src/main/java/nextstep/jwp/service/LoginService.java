package nextstep.jwp.service;

import nextstep.jwp.LoginFailureException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    public User login(String account, String password) {
        User existedUser = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(LoginFailureException::new);
        if (!existedUser.checkPassword(password)) {
            throw new LoginFailureException();
        }
        log.info("로그인 성공! 아이디: " + account);
        return existedUser;
    }
}
