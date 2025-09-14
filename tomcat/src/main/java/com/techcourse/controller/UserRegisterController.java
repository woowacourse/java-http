package com.techcourse.controller;

import com.techcourse.service.UserService;
import org.apache.coyote.http11.handler.AbstractController;
import org.apache.coyote.http11.handler.applicationRequest.ApplicationRequest;
import org.apache.coyote.http11.handler.applicationResponse.ApplicationResponse;
import org.apache.coyote.http11.httpRequest.HttpRequest;

public class UserRegisterController extends AbstractController {

    private final UserService userService;

    public UserRegisterController(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected ApplicationResponse doPost(ApplicationRequest applicationRequest) {
        return userService.register(applicationRequest);
    }

    @Override
    protected ApplicationResponse doGet(ApplicationRequest applicationRequest) {
        return userService.registerPage();
    }
}
