package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class UserService {

    public User find(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("계정이 없습니다."));
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }
        return user;
    }

    public void save(String account, String email, String password) {
        InMemoryUserRepository.save(new User(account, password, email));
    }
}
