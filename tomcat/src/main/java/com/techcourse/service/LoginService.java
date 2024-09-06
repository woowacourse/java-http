package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.request.UserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    public void findUser(UserRequest request) {
        User account = InMemoryUserRepository.findByAccount(request.getAccount())
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않아요 ㅜㅜ"));
        log.info("user : {}", account);
    }
}
