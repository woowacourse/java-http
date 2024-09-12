package com.techcourse.controller;

import com.techcourse.service.UserService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {


    private static final String DEFAULT_REQUEST_URI = "/index";
    private static final String REGISTER_REQUEST_URI = "/register";
    private static final String UNAUTHORIZED_REQUEST_URI = "/401";

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        try {
            RequestBody body = request.getBody();
            userService.create(body.getParameters());
            response.sendRedirect(DEFAULT_REQUEST_URI);
        } catch (IllegalArgumentException e) {
            response.sendRedirect(UNAUTHORIZED_REQUEST_URI);
        }
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.addStaticResource(REGISTER_REQUEST_URI);
    }
}
