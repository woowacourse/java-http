package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.AuthenticationException;
import com.techcourse.model.User;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public User login(String account, String password) {
        User user = Optional.ofNullable(account)
                .flatMap(InMemoryUserRepository::findByAccount)
                .orElseThrow(() -> new AuthenticationException("존재하지 않는 계정입니다."));
        user.authenticate(password);
        log.info("user : {}", user);
        return user;
    }

    public void register(String account, String password, String email) {
        InMemoryUserRepository.save(new User(account, password, email));
    }
}
