package com.techcourse.controller;

import com.techcourse.service.UserService;
import org.apache.coyote.http11.handler.AbstractController;
import org.apache.coyote.http11.handler.applicationResponse.ApplicationResponse;
import org.apache.coyote.http11.httpRequest.HttpRequest;

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
