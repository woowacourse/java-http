package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.InvalidRegisterException;
import com.techcourse.exception.UnauthorizedException;
import com.techcourse.model.User;

public class UserService {

    public User login(String account, String password) {
        if (account.isEmpty() || password.isEmpty()) {
            throw new UnauthorizedException("Values for authorization is missing.");
        }

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new UnauthorizedException("Cannot find account"));

        if (!user.checkPassword(password)) {
            throw new UnauthorizedException("Wrong password");
        }

        return user;
    }

    public User register(String account, String password, String email) {
        if (account.isEmpty() || password.isEmpty() || email.isEmpty()) {
            throw new InvalidRegisterException("Values for register is missing.");
        }
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        return user;
    }
}
