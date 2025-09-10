package com.techcourse.controller;

import com.techcourse.service.UserService;
import java.io.IOException;
import org.apache.coyote.http11.handler.AbstractController;
import org.apache.coyote.http11.handler.controllerResponse.ApplicationResponse;
import org.apache.coyote.http11.httpRequest.HttpRequest;
import org.apache.coyote.http11.httpResponse.HttpResponse;

public class UserRegisterController extends AbstractController {

    private final UserService userService;

    public UserRegisterController(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected ApplicationResponse doPost(HttpRequest httpRequest) {
        return userService.register(httpRequest);
    }

    @Override
    protected ApplicationResponse doGet(HttpRequest httpRequest) {
        return userService.registerPage();
    }
}
