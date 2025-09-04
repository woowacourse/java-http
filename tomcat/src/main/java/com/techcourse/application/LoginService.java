package com.techcourse.application;

import com.techcourse.application.dto.LoginRequest;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.BusinessException;
import com.techcourse.exception.ErrorCode;
import com.techcourse.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    public void login(LoginRequest request) {
        User user = InMemoryUserRepository.findByAccount(request.account())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (user.checkPassword(request.password())) {
            log.info(user.toString());
        }
    }
}
