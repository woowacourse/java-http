package com.techcourse.controller;

import com.techcourse.application.AuthService;
import com.techcourse.application.dto.LoginRequest;

public class LoginController {
    private final AuthService authService = new AuthService();

    public void login(LoginRequest request) {
        authService.login(request);
    }
}
