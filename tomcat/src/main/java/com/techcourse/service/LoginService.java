package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;

public class LoginService {

    public boolean login(String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
                .map(user -> user.checkPassword(password))
                .orElse(false);
    }
}
