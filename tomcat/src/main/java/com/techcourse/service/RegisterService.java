package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterService {

    public void registerUser(String account, String password, String email) {
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        InMemoryUserRepository.save(new User(account, password, email));
    }
}
