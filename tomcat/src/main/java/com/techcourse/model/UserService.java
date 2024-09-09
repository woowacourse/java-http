package com.techcourse.model;

import com.techcourse.db.InMemoryUserRepository;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public void login(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new NoSuchElementException("계정 정보가 틀렸습니다."));
        if (!user.checkPassword(password)) {
            throw new NoSuchElementException("계정 정보가 틀렸습니다.");
        }
        log.info("user: {}", user);
    }
}
