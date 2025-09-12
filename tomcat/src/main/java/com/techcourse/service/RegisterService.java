package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterService {

    private static final Logger log = LoggerFactory.getLogger(RegisterService.class);

    public void register(final String account, final String password, final String email) {
        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("register success!: account={}", account);
    }
}
