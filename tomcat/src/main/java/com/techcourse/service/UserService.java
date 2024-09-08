package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class UserService {

    public User findUser(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (user.checkPassword(password)) {
            return user;
        }
        throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }
}
