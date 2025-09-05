package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.catalina.AbstractController;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Override
    public void toGet(Http11Request request, Http11Response response) {
        final String account = request.parseQuery().get("account");
        if (account != null && !account.isBlank()) {
            final var user = InMemoryUserRepository.findByAccount(account);
            user.ifPresent(value -> log.info("User found: {}", value));
        }
        response.setResourcePath("/login.html");
    }

    @Override
    public void toPost(Http11Request request, Http11Response response) {
        //Todo: step2 기능 추가 예정 [2025-09-05 16:26:11]
        InMemoryUserRepository.save(null);
    }
}
