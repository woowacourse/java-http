package com.techcourse.controller;

import java.io.IOException;
import java.util.Map;

import org.apache.coyote.http11.Http11Helper;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UnauthorizedException;
import com.techcourse.model.User;
import com.techcourse.service.UserService;

public class LoginController extends Controller {
    private static final LoginController instance = new LoginController();
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final UserService userService = new UserService();
    private final Http11Helper http11Helper = Http11Helper.getInstance();

    private LoginController() {
    }

    public static LoginController getInstance() {
        return instance;
    }

    @Override
    public String handle(HttpRequest request) throws IOException {
        try {
            String response = operate(request);

            return response;
        } catch (UnauthorizedException e) {
            log.error("Error processing request for endpoint: {}", request.getURI(), e);

            String response = http11Helper.createResponse(HttpStatus.UNAUTHORIZED, "401.html");
            return response;
        }
    }

    @Override
    protected String doPost(HttpRequest request) throws IOException {
        Map<String, String> requestBody = request.getBody().parseRequestBody();
        String account = requestBody.get("account");
        String password = requestBody.get("password");

        User user = userService.login(account, password);
        log.info("User found: {}", user);

        String response = http11Helper.createResponse(HttpStatus.FOUND, "index.html");

        return response;
    }

    @Override
    protected String doGet(HttpRequest request) throws IOException {
        String endpoint = request.getURI();
        String fileName = http11Helper.getFileName(endpoint);
        String response = http11Helper.createResponse(HttpStatus.OK, fileName);

        return response;
    }
}

