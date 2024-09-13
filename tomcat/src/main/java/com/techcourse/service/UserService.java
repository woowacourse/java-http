package com.techcourse.service;

import com.techcourse.controller.dto.LoginRequest;
import com.techcourse.controller.dto.RegisterRequest;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final UserService INSTANCE = new UserService();
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private UserService() {
    }

    public static UserService getInstance() {
        return INSTANCE;
    }

    public Optional<User> login(LoginRequest loginRequest) {
        String userAccount = loginRequest.account();
        String userPassword = loginRequest.password();
        log.info("로그인 요청 -  id: {}, password: {}", userAccount, userPassword);

        return InMemoryUserRepository.findByAccount(userAccount)
                .filter(user -> user.checkPassword(userPassword));
    }

    public void register(RegisterRequest registerRequest) {
        String account = registerRequest.account();
        String password = registerRequest.password();
        String email = registerRequest.email();
        log.info("회원가입 요청 -  id: {}, password: {}, email: {}", account, password, email);

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }
}
