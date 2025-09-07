package com.techcourse.controller;

import com.techcourse.service.UserService;
import org.apache.coyote.http11.handler.controllerResponse.ControllerResponse;
import org.apache.coyote.http11.httpRequest.HttpRequest;

public class UserController {

    private final UserService userService = new UserService();

    public ControllerResponse loginGet(HttpRequest httpRequest) {
        return userService.loginPage(httpRequest);
    }

    public ControllerResponse loginPost(HttpRequest httpRequest) {
        return userService.login(httpRequest);
    }

    public ControllerResponse registerGet(HttpRequest httpRequest) {
        return userService.register(httpRequest);
    }
}
