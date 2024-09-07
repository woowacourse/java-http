package org.apache.coyote.http11.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public User save(User user) {
        InMemoryUserRepository.save(user);
        log.info("새로운 user: {}", user);
        return InMemoryUserRepository.findByAccount(user.getAccount())
                .orElseThrow(() -> new IllegalStateException("신규 유저 저장에 실패했습니다."));
    }
}
