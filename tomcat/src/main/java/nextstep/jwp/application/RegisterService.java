package nextstep.jwp.application;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterService {

    private static final Logger log = LoggerFactory.getLogger(RegisterService.class);

    public void register(String account, String email, String password) {
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("회원가입 성공! - {}", user);
    }
}
