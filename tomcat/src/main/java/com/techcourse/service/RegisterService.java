package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;

public class RegisterService {

    public void addRegister(String account, String password, String email) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }
        InMemoryUserRepository.save(new User(account, password, email));
    }
}
