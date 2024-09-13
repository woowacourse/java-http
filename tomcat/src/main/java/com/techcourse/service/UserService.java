package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.except.UnauthorizedException;
import com.techcourse.except.UserNotFoundException;
import com.techcourse.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private static final UserService INSTANCE = new UserService();

    private UserService() {
    }

    public static UserService getInstance() {
        return INSTANCE;
    }

    public boolean isAccountExist(String account) {
        return InMemoryUserRepository.findByAccount(account).isPresent();
    }

    public void checkUser(User user, String password) {
        if (!user.checkPassword(password)) {
            throw new UnauthorizedException("비밀번호가 일치하지 않습니다.");
        }
    }

    public User findBy(String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 회원입니다."));
    }

    public void registerUser(String account, String password, String email) {
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        LOGGER.info("회원가입 성공: {}", user.getAccount());
    }
}
