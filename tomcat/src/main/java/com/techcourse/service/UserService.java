package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.model.UserAccount;
import com.techcourse.model.UserEmail;
import com.techcourse.model.UserPassword;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public User findUser(Map<String, String> params) {
        UserAccount account = new UserAccount(params.getOrDefault("account", ""));
        UserPassword password = new UserPassword(params.getOrDefault("password", ""));

        User user = InMemoryUserRepository.findByAccount(account.account())
                .orElseThrow(() -> new IllegalArgumentException("유저 정보가 존재하지 않습니다."));

        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        log.info("user : {}", user);
        return user;
    }

    public void create(Map<String, String> params) {
        UserAccount account = new UserAccount(params.getOrDefault("account", ""));
        UserPassword password = new UserPassword(params.getOrDefault("password", ""));
        UserEmail email = new UserEmail(params.getOrDefault("email", ""));

        if (InMemoryUserRepository.findByAccount(account.account()).isPresent()) {
            throw new IllegalArgumentException("이미 등록된 계정으로 회원가입할 수 없습니다.");
        }

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("User Saved: {}", user);
    }
}
