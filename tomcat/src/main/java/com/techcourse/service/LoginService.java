package com.techcourse.service;

import java.util.Optional;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class LoginService {

    public User login(String account, String password) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            return user.get();
        }
        throw new IllegalStateException("로그인 정보가 잘못되었습니다.");
    }
}
