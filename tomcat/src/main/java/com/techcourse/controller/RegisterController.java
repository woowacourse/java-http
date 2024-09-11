package com.techcourse.controller;

import java.io.IOException;

import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.InvalidRegisterException;
import com.techcourse.model.User;
import com.techcourse.service.UserService;

public class RegisterController extends Controller {
    private static final RegisterController instance = new RegisterController();
    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    private final UserService userService = new UserService();

    private RegisterController() {
    }

    public static RegisterController getInstance() {
        return instance;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        try {
            operate(request, response);
        } catch (InvalidRegisterException e) {
            log.error("Error processing request for endpoint: {}", request.getURI(), e);

            redirect("400.html", response);
        }
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        RequestBody requestBody = request.getBody();
        String account = requestBody.getAttribute("account");
        String password = requestBody.getAttribute("password");
        String email = requestBody.getAttribute("email");

        User user = userService.register(account, password, email);
        log.info("User registered: {}", user);

        redirect("index.html", response);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        redirect("register.html", response);
    }

    private static void redirect(String location, HttpResponse response) {
        response.setStatus(HttpStatus.FOUND);
        response.setLocation(location);
    }
}
