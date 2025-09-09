package com.techcourse;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Service {

    private static final Logger log = LoggerFactory.getLogger(Service.class);

    public User findUser(Map<String, String> params) {
        User user = findUserByAccount(params.get("account"));

        log.info("Found user: " + user.toString());

        return user;
    }

    public User registerUser(final String account, final String password, final String email) {
        InMemoryUserRepository.save(new User(account, password, email));
        return findUserByAccount(account);
    }

    private User findUserByAccount(final String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }
}
