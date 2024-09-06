package com.techcourse.controller;

import java.io.IOException;
import java.util.Map;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
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

            return redirect("401.html");
        }
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws IOException {
        Map<String, String> requestBody = request.getBody().parseRequestBody();
        String account = requestBody.get("account");
        String password = requestBody.get("password");

        User user = userService.login(account, password);
        log.info("User found: {}", user);

        return redirect("index.html");
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        return redirect("login.html");
    }

    private static HttpResponse redirect(String location) {
        HttpResponse response = new HttpResponse();
        response.setStatus(HttpStatus.FOUND);
        response.setLocation(location);
        return response;
    }
}

