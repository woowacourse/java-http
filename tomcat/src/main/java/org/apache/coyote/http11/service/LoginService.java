package org.apache.coyote.http11.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {
    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    public void checkLogin(String account, String password) {
        User user = findByAccount(account);

        if (!user.checkPassword(password)) {
            throw new SecurityException("잘못된 유저 정보 입니다.");
        }

        log.info("user : {}", user);
    }

    public User findByAccount(String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new SecurityException("잘못된 유저 정보 입니다."));
    }
}
