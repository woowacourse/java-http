package com.techcourse.service;

import java.util.Optional;

import com.techcourse.model.User;

public class LoginService {

    private final AuthService authService;

    public LoginService(AuthService authService) {
        this.authService = authService;
    }

    public Optional<User> login(String account, String password) {
        return authService.authenticateUser(account, password);
    }
}
