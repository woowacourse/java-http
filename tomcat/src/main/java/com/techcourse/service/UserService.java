package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.ConflictException;
import com.techcourse.exception.NotFoundAccountException;
import com.techcourse.exception.UnAuthorizedException;
import com.techcourse.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public static User login(final String account, final String password) {
        User user = findByAccount(account);
        if (user.checkPassword(password)) {
            log.info(user.toString());
            return user;
        }
        log.info("User login failed: {}, {}", account, password);
        throw new UnAuthorizedException("Login failed");
    }

    public static void register(final String account, final String email, final String password) {
        User user = new User(account, email, password);
        if (existsByAccount(account)) {
            throw new ConflictException("Account already exists");
        }
        log.info("User register: {}", user.getAccount());
        InMemoryUserRepository.save(user);
    }

    private static User findByAccount(final String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new NotFoundAccountException("Account not found"));
    }

    private static boolean existsByAccount(final String account) {
        return InMemoryUserRepository.existsByAccount(account);
    }
}
