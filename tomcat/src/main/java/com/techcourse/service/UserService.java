package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UnauthorizedException;
import com.techcourse.model.User;

public class UserService {

    public User findUserByAccountAndPassword(String account, String password) {
        return InMemoryUserRepository.findByAccountAndPassword(account, password)
                .orElseThrow(() -> new UnauthorizedException("id/pw 가 올바르지 않습니다."));
    }
}
