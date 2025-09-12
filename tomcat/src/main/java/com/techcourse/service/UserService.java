package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public static User login(String account, String password) {
        User user = findUser(account, password);
        checkPassword(user, password);
        log.info(user.toString());

        return user;
    }

    private static void checkPassword(User user, String password) {
        if (!user.checkPassword(password)) {
            log.info("password does not match account : {} password : {}", user.getAccount(), password);
            throw new IllegalArgumentException("password does not match");
        }
    }

    private static User findUser(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() ->
                        new IllegalArgumentException("user not found"));

        return user;
    }

    public static void createUser(String account, String password, String email) {
        User savedUser = new User(account, password, email);
        InMemoryUserRepository.save(savedUser);
    }
}
