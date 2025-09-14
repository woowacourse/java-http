package com.techcourse.controller;

import com.techcourse.service.UserService;
import org.apache.coyote.http11.handler.AbstractController;
import org.apache.coyote.http11.handler.applicationRequest.ApplicationRequest;
import org.apache.coyote.http11.handler.applicationResponse.ApplicationResponse;

public class UserLoginController extends AbstractController {

    private final UserService userService;

    public UserLoginController(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected ApplicationResponse doPost(ApplicationRequest applicationRequest) {
        return userService.login(applicationRequest);
    }

    @Override
    protected ApplicationResponse doGet(ApplicationRequest applicationRequest) {
        return userService.loginPage(applicationRequest);
    }
}
