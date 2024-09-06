package com.techcourse.controller;

import java.io.IOException;
import java.util.Map;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
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

    private LoginController() {
    }

    public static LoginController getInstance() {
        return instance;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        try {
            HttpResponse response = operate(request);
            return response;
        } catch (UnauthorizedException e) {
            log.error("Error processing request for endpoint: {}", request.getURI(), e);

            HttpResponse response = new HttpResponse();
            response.setStatus(HttpStatus.FOUND);
            response.setLocation("401.html");
            return response;
        }
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws IOException {
        Map<String, String> requestBody = request.getBody().parseRequestBody();
        String account = requestBody.get("account");
        String password = requestBody.get("password");

        User user = userService.login(account, password);
        log.info("User found: {}", user);

        HttpResponse response = new HttpResponse();
        response.setStatus(HttpStatus.FOUND);
        response.setLocation("index.html");
        return response;
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        HttpResponse response = new HttpResponse();
        response.setStatus(HttpStatus.FOUND);
        response.setLocation("login.html");

        return response;
    }
}

