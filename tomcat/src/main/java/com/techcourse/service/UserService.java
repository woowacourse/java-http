package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UnauthorizedException;
import com.techcourse.model.User;

public class UserService {

    public User login(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new UnauthorizedException("Cannot find account"));

        if (!user.checkPassword(password)) {
            throw new UnauthorizedException("Wrong password");
        }
        return user;
    }
}
