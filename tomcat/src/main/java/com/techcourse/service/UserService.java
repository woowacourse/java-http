package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public Optional<User> login(String account, String password) {
        if (account == null || password == null) {
            return Optional.empty();
        }

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            log.info("user : {}", user.get());
            return user;
        }

        return Optional.empty();
    }

    public void register(String account, String password, String email) {
        InMemoryUserRepository.save(new User(account, password, email));
    }
}
