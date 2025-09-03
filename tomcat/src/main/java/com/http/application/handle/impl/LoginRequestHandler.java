package com.http.application.handle.impl;

import com.http.application.handle.RequestHandler;
import com.http.domain.HttpRequest;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginRequestHandler implements RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginRequestHandler.class);

    @Override
    public void handle(HttpRequest httpRequest) {
        final Map<String, String> queryStrings = httpRequest.queryStrings();

        String account = queryStrings.get("account");
        String password = queryStrings.get("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));

        if (user.checkPassword(password)) {
            log.info("user : {} ", user);
        }
    }
}
