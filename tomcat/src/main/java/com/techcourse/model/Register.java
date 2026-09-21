package com.techcourse.model;

import com.techcourse.db.InMemoryUserRepository;
import java.util.Optional;

public class Register {

    public static User register(String account, String email, String password) {
        User user = new User(account, password, email);
        if (isAlreadyRegistered(user)) {
            throw new IllegalArgumentException("이미 가입된 계정입니다.");
        }
        InMemoryUserRepository.save(user);
        return user;
    }

    private static boolean isAlreadyRegistered(User user) {
        Optional<User> foundUser = InMemoryUserRepository.findByAccount(user.getAccount());
        return foundUser.isPresent();
    }

    private Register() {
    }
}
