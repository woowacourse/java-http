package nextstep.jwp.application;

import static nextstep.jwp.db.InMemoryUserRepository.findByAccount;

import java.util.Optional;
import nextstep.jwp.exception.InvalidPasswordException;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    public User login(String account, String password) {
        Optional<User> foundUser = findByAccount(account);
        User user = foundUser.orElseThrow();

        if (!user.checkPassword(password)) {
            log.debug("비밀번호 틀림! - 입력={}", password);
            throw new InvalidPasswordException();
        }
        log.info("로그인 성공! - {}", user);
        return user;
    }
}
