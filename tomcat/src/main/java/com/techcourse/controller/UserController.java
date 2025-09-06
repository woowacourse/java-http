package com.techcourse.controller;

import com.techcourse.service.UserService;
import org.apache.coyote.http11.httpRequest.HttpRequest;
import org.apache.coyote.http11.httpResponse.HttpResponse;

public class UserController {

    private final UserService userService = new UserService();

    public HttpResponse login(HttpRequest httpRequest) {
        return userService.login(httpRequest);
    }
}
