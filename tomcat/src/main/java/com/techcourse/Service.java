package com.techcourse;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.time.LocalDateTime;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Service {

    private static final Logger log = LoggerFactory.getLogger(Service.class);

    public User getLoggedInUser(final Map<String, String> params) {
        User user = findUserByAccount(params.get("account"));

        if (user.checkPassword(params.get("password"))) {
            log.info("User [{}] logged in at {}", user.getAccount(), LocalDateTime.now());
            return user;
        }

        throw new IllegalArgumentException("유효한 로그인 정보가 아닙니다.");
    }

    private User findUserByAccount(final String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    public User registerUser(final String account, final String password, final String email) {
        InMemoryUserRepository.save(new User(account, password, email));
        return findUserByAccount(account);
    }
}
