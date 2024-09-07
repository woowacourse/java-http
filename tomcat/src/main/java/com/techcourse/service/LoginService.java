package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.client.UnauthorizedException;
import com.techcourse.model.User;

public class LoginService {

    public static User login(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new UnauthorizedException("존재하지 않는 계정입니다."));
        validateUser(user, password);
        return user;
    }

    private static void validateUser(User user, String password) {
        if (!user.checkPassword(password)) {
            throw new UnauthorizedException("비밀번호가 일치하지 않습니다.");
        }
    }
}
