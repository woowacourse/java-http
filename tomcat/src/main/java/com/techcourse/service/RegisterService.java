package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.client.BadRequestException;
import com.techcourse.model.User;

public class RegisterService {

    private RegisterService() {}

    public static void addUser(String account, String password, String email) {
        InMemoryUserRepository.findByAccount(account)
                .ifPresent(user -> {throw new BadRequestException("이미 존재하는 이메일입니다.");});
        InMemoryUserRepository.save(new User(account, password, email));
    }
}
