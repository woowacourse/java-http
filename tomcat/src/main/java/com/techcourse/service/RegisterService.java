package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterService {

    private static final Logger log = LoggerFactory.getLogger(RegisterService.class);

    public boolean isExistAccount(String account) {
        return InMemoryUserRepository.existByAccount(account);
    }

    public void register(String account, String password, String email) {
        User user = new User(account, password, email);
        if (isExistAccount(account)) {
            log.debug(account + ": already exist account");
            return;
        }

        log.debug("{}", user);
        InMemoryUserRepository.save(user);
    }
}
