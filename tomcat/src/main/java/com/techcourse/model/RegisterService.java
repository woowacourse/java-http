package com.techcourse.model;

import com.techcourse.db.InMemoryUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterService {

    private static final Logger log = LoggerFactory.getLogger(RegisterService.class);

    public void register(final String account, final String password, final String email) {
        final var user = User.of(
                account,
                password,
                email
        );

        InMemoryUserRepository.save(user);
        log.info("register user: {}", user);
    }
}
