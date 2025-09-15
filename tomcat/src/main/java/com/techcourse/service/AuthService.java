package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class AuthService {

    public User login(final String account, final String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .orElseThrow(IllegalArgumentException::new);
    }

    public User register(final String account, final String password, final String email) {
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        return user;
    }
}
