package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.dto.RegisterDto;
import com.techcourse.model.User;

public class RegisterService {

    public void save(RegisterDto registerDto) {
        validateAccount(registerDto.account());
        InMemoryUserRepository.save(new User(registerDto.account(), registerDto.password(), registerDto.email()));
    }

    private void validateAccount(String account) {
        InMemoryUserRepository.findByAccount(account).ifPresent(user -> {
            throw new IllegalArgumentException(user.getAccount() + " 계정이 이미 존재합니다.");
        });
    }
}
