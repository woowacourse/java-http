package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;

public class Service {

    public User getUser(Map<String, String> loginRequest) {
        User user = InMemoryUserRepository.findByAccount(loginRequest.get("account"))
                .orElseThrow(() -> new IllegalArgumentException("User not found : " + loginRequest.get("account")));

        if (user.checkPassword(loginRequest.get("password"))) {
            return user;
        }

        throw new IllegalArgumentException("invalid password");
    }

    public void create(Map<String, String> signInRequest) {
        String account = signInRequest.get("account");
        String password = signInRequest.get("password");
        String email = signInRequest.get("email");
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }
}
