package com.techcourse.service;

import java.util.Optional;

import org.apache.coyote.http11.common.Session;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UnauthorizedException;
import com.techcourse.model.User;

public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public User login(Parameters requestBody) {
        String account = requestBody.get("account");
        String password = requestBody.get("password");
        User user = findUser(account, password);
        log.info(user.toString());
        return user;
    }

    private User findUser(String account, String password) {
        if (account == null || password == null) {
            throw new UnauthorizedException("필수 정보가 누락되었습니다.");
        }
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty()) {
            throw new UnauthorizedException("존재하지 않는 사용자입니다.");
        }
        if (!user.get().checkPassword(password)) {
            throw new UnauthorizedException("비밀번호가 틀렸습니다.");
        }
        return user.get();
    }

    public void register(HttpRequest request) {
        Parameters parameters = request.getBody();
        String account = parameters.get("account");
        String email = parameters.get("email");
        String password = parameters.get("password");
        InMemoryUserRepository.findByAccount(account)
            .ifPresent(user -> {
                throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
            });
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }

    public User getLoggedUser(Session session) {
        if (session == null) {
            return null;
        }
        return (User)session.getAttribute("user");
    }
}
