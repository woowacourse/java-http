package com.techcourse.application;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    private AtomicLong index = new AtomicLong(1);

    public User login(final String account, final String password) {
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 사용자가 존재하지 않습니다."));

        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("일치하는 사용자가 존재하지 않습니다.");
        }

        log.info("user: {}", user);
        return user;
    }

    public User register(final String account, final String password, final String email) {
        final Optional<User> existUser = InMemoryUserRepository.findByAccount(account);
        if (existUser.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        final User user = new User(index.incrementAndGet(), account, password, email);
        InMemoryUserRepository.save(user);
        return user;
    }
}
