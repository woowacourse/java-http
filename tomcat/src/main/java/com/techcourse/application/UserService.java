package com.techcourse.application;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class UserService {

    public User login(String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .orElseThrow(() -> new IllegalArgumentException("로그인 실패"));
    }
}
