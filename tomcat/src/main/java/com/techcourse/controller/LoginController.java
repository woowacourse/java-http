package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    public LoginController() {
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        String requestAccount = request.getQueryData("account");
        String requestPassword = request.getQueryData("password");
        InMemoryUserRepository.findByAccount(requestAccount)
                .filter(user -> user.checkPassword(requestPassword))
                .ifPresent(user -> LOGGER.info(user.toString()));
        response.setResourceName("/login.html");
    }
}
