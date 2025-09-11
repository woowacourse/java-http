package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UnauthorizedException;
import com.techcourse.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    public User login(final String account, final String password) {
        final User user = getUser(account);
        checkPassword(password, user);

        log.info("user : {}", user);
        return user;
    }

    private void checkPassword(final String password, final User user) {
        if (!user.checkPassword(password)) {
            log.warn("유효하지 않은 password입니다: {}", user.getAccount());
            throw new UnauthorizedException("유효하지 않은 password입니다: %s".formatted(user.getAccount()));
        }
    }

    private User getUser(final String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 유저입니다: {}", account);
                    return new UnauthorizedException("존재하지 않는 유저입니다: %s".formatted(account));
                });
    }
}
