package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public void login(final String account, final String password) {
        User user = findUser(account);
        validatePassword(password, user);
        log.info(account + " 로그인 성공!");
    }

    private User findUser(final String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("계정이 없습니다."));
    }

    private void validatePassword(final String password, final User user) {
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("잘못된 패스워드입니다.");
        }
    }
}
