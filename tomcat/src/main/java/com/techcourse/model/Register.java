package com.techcourse.model;

import com.techcourse.db.InMemoryUserRepository;

public class Register {

    public static synchronized User register(String account, String email, String password) {
        User user = new User(account, password, email);
        if (isAlreadyRegistered(user)) {
            throw new IllegalArgumentException("이미 가입된 계정입니다.");
        }
        InMemoryUserRepository.save(user);
        return user;
    }

    private static boolean isAlreadyRegistered(User user) {
        return InMemoryUserRepository.findByAccount(user.getAccount())
                .isPresent();
    }

    private Register() {
    }
}
