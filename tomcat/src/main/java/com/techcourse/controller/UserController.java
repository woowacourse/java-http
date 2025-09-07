package com.techcourse.controller;

import com.techcourse.service.UserService;
import org.apache.coyote.http11.handler.controllerResponse.ControllerResponse;
import org.apache.coyote.http11.httpRequest.HttpRequest;

public class UserController {

    private final UserService userService = new UserService();

    public ControllerResponse login(HttpRequest httpRequest) {
        return userService.login(httpRequest);
    }

    public ControllerResponse register(HttpRequest httpRequest) {
        return userService.register(httpRequest);
    }
}
