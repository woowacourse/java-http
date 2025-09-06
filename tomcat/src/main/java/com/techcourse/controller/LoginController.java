package com.techcourse.controller;

import com.techcourse.application.LoginService;
import com.techcourse.application.dto.LoginRequest;

public class LoginController {
    private final LoginService loginService = new LoginService();

    public void login(LoginRequest request) {
        loginService.login(request);
    }
}
