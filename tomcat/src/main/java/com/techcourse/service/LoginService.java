package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class LoginService {

    public User login(String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .orElseThrow(() -> new IllegalStateException("로그인 정보가 잘못되었습니다."));
    }
}
