package com.techcourse.application;

import com.techcourse.application.dto.LoginRequest;
import com.techcourse.application.dto.RegisterRequest;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.BusinessException;
import com.techcourse.exception.ErrorCode;
import com.techcourse.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    public User login(LoginRequest request) {
        User user = InMemoryUserRepository.findByAccount(request.account())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        validatePassword(request, user);
        return user;
    }

    public void register(RegisterRequest request) {
        User user = new User(request.account(), request.password(), request.email());
        InMemoryUserRepository.save(user);
    }

    private void validatePassword(LoginRequest request, User user) {
        if (!user.checkPassword(request.password())) {
            throw new BusinessException(ErrorCode.PASSWORD_NOT_MATCHED);
        }
    }

    public void loginCheck(String account) {
        log.info("account: " + account);
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
