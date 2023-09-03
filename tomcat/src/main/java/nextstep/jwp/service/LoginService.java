package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.InvalidLoginInfoException;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {

    private final Logger log = LoggerFactory.getLogger(LoginService.class);

    public void login(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow();
        if (!user.checkPassword(password)) {
            throw new InvalidLoginInfoException();
        }
        log.info(user.toString());
    }

    public void register(String account, String password, String email) {
        InMemoryUserRepository.save(new User(account, password, email));
    }
}
