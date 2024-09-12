package com.techcourse.service;

import java.util.Optional;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class LoginService {

    public Optional<User> login(String account, String password) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            return user;
        }
        return Optional.empty();
    }
}
