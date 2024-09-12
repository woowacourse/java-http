package com.techcourse.app.service;

import com.techcourse.app.db.InMemoryUserRepository;
import com.techcourse.app.exception.AuthenticationException;
import com.techcourse.app.exception.DuplicatedException;
import com.techcourse.app.model.User;

public class UserService {

    public User login(String id, String password) {
        User user = InMemoryUserRepository.findByAccount(id)
                .orElseThrow(() -> new AuthenticationException("사용자를 찾을 수 없습니다."));

        if (!user.checkPassword(password)) {
            throw new AuthenticationException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }

    public User register(String account, String password, String email) {
        InMemoryUserRepository.findByAccount(account)
                .ifPresent(user -> {
                    throw new DuplicatedException("이미 존재하는 계정입니다.");
                });

        return InMemoryUserRepository.save(new User(account, password, email));
    }
}
