package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.coyote.http11.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Controller {

    private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);

    public Controller() {
    }

    public String getLogin(HttpRequest request) {
        String requestAccount = request.getQueryStringData("account");
        String requestPassword = request.getQueryStringData("password");
        InMemoryUserRepository.findByAccount(requestAccount)
                .ifPresent(user -> {
                    if (user.checkPassword(requestPassword)) {
                        LOGGER.info(user.toString());
                    }
                });
        return "/login.html";
    }
}
