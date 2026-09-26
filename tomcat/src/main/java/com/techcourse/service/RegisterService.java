package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterService {

    public boolean register(String account, String password, String email) {
        if (account == null || account.isBlank()
                || password == null || password.isBlank()
                || email == null || email.isBlank()) {
            return false;
        }

        InMemoryUserRepository.save(new User(account, password, email));
        return true;
    }
}
