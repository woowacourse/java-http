package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.dto.LoginRequestDto;
import com.techcourse.dto.RegisterRequestDto;
import com.techcourse.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    public void register(RegisterRequestDto registerRequestDto) {
        InMemoryUserRepository.save(new User(
                registerRequestDto.account(), registerRequestDto.password(), registerRequestDto.email()));
    }

    public User login(LoginRequestDto loginRequestDto) {
        User user = InMemoryUserRepository.findByAccount(loginRequestDto.account())
                .orElseThrow(() -> new IllegalArgumentException("Account not found or wrong password"));
        if (!user.checkPassword(loginRequestDto.password())) {
            throw new IllegalArgumentException("Account not found or wrong password");
        }
        log.info("Logged in user: {}", user);
        return user;
    }
}
