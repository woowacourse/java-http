package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.catalina.AbstractController;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Override
    public void toGet(Http11Request request, Http11Response response) {
        String account = request.parseQuery().get("account");
        if (account != null && !account.isBlank()) {
            Optional<User> user = InMemoryUserRepository.findByAccount(account);
            user.ifPresent(value -> log.info("User found: {}", value));
        }
        response.setResourcePath("/login.html");
    }

    @Override
    public void toPost(Http11Request request, Http11Response response) {
        InMemoryUserRepository.save(null);

    }
}
