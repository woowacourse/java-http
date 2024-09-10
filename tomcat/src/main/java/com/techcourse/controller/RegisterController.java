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
    public HttpResponse handle(HttpRequest request) throws IOException {
        try {
            HttpResponse response = operate(request);
            return response;
        } catch (InvalidRegisterException e) {
            log.error("Error processing request for endpoint: {}", request.getURI(), e);

            return redirect("400.html");
        }
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws IOException {
        RequestBody requestBody = request.getBody();
        String account = requestBody.getAttribute("account");
        String password = requestBody.getAttribute("password");
        String email = requestBody.getAttribute("email");

        User user = userService.register(account, password, email);
        log.info("User registered: {}", user);

        return redirect("index.html");
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        return redirect("register.html");
    }

    private static HttpResponse redirect(String location) {
        HttpResponse response = new HttpResponse();
        response.setStatus(HttpStatus.FOUND);
        response.setLocation(location);

        return response;
    }
}
