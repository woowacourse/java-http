package com.techcourse.model;

import com.techcourse.db.InMemoryUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    public boolean login(final String account, final String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> {
                    log.info("user: {}", user);
                    
                    return true;
                })
                .orElse(false);
    }
}
