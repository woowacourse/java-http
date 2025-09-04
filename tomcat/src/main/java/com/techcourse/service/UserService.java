package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.NotFoundAccountException;
import com.techcourse.exception.UnAuthorizedException;
import com.techcourse.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public static void login(final String account, final String password) {
        User user = findByAccount(account);
        if (user.checkPassword(password)) {
            log.info(user.toString());
            return;
        }
        throw new UnAuthorizedException("Login failed");
    }

    private static User findByAccount(final String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new NotFoundAccountException("Account not found"));
    }
}
