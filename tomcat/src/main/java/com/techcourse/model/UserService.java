package com.techcourse.model;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.TechcourseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private static final UserService INSTANCE = new UserService();

    private UserService() {
    }

    public static UserService getInstance() {
        return INSTANCE;
    }

    public User login(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new TechcourseException("계정 정보가 틀렸습니다."));
        if (!user.checkPassword(password)) {
            throw new TechcourseException("계정 정보가 틀렸습니다.");
        }
        log.info("user: {}", user);
        return user;
    }

    public void register(String account, String password, String email) {
        if (InMemoryUserRepository.existsByAccount(account)) {
            throw new TechcourseException("이미 존재하는 계정입니다. account 값은 중복될 수 없습니다.");
        }
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("user: {}", user);
    }
}
