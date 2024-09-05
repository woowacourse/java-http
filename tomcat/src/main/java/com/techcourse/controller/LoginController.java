package com.techcourse.controller;

import java.io.IOException;
import java.util.Map;

import org.apache.coyote.http11.Http11Helper;
import org.apache.coyote.http11.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UnauthorizedException;
import com.techcourse.model.User;
import com.techcourse.service.UserService;

public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private final UserService userService = new UserService();
    private final Http11Helper http11Helper = Http11Helper.getInstance();

    public String login(String request) throws IOException {
        String endpoint = http11Helper.extractEndpoint(request);
        Map<String, String> queryParams = http11Helper.parseQueryString(endpoint);
        String account = queryParams.get("account");
        String password = queryParams.get("password");

        if (account == null || password == null) {
            throw new UnauthorizedException("Values for authorization is missing.");
        }

        User user = userService.login(account, password);
        log.info("User found: {}", user);

        String response = http11Helper.createResponse(HttpStatus.FOUND, "index.html");

        return response;
    }
}

