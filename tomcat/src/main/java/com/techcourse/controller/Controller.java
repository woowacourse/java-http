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
        String requestAccount = request.getQueryData("account");
        String requestPassword = request.getQueryData("password");
        InMemoryUserRepository.findByAccount(requestAccount)
                .filter(user -> user.checkPassword(requestPassword))
                .ifPresent(user -> LOGGER.info(user.toString()));
        return "/login.html";
    }
}
