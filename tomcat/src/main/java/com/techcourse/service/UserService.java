package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.AuthenticationException;
import com.techcourse.model.User;

public class UserService {

    public UserResponse login(String id, String password) {
        User user = InMemoryUserRepository.findByAccount(id)
                .orElseThrow(() -> new AuthenticationException("사용자를 찾을 수 없습니다."));

        if (!user.checkPassword(password)) {
            throw new AuthenticationException("비밀번호가 일치하지 않습니다.");
        }

        return UserResponse.from(user);
    }
}
