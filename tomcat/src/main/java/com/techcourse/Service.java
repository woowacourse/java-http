package com.techcourse;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;

public class Service {

    public User getUser(Map<String, String> loginRequest) {
        User user = InMemoryUserRepository.findByAccount(loginRequest.get("account"))
                .orElseThrow(IllegalArgumentException::new);

        if (user.checkPassword(loginRequest.get("password"))) {
            return user;
        }

        throw new IllegalArgumentException("invalid password");
    }
}
