package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class LoginService {

    public User authenticate(String account, String password) {
        if (account == null || account.isBlank()
                || password == null || password.isBlank()) {
            return null;
        }

        User user = InMemoryUserRepository.findByAccount(account).orElse(null);
        if (user == null || !user.checkPassword(password)) {
            return null;
        }
        return user;
    }
}
