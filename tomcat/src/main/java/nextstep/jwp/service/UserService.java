package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public void login(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정입니다."));

        if (user.checkPassword(password)) {
            log.info("User: {}", user);
            return;
        }

        throw new IllegalArgumentException("잘못된 패스워드입니다.");
    }
}
