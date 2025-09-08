package com.techcourse.controller;

import com.techcourse.model.User;
import com.techcourse.service.UserService;

public class LoginController {

    private final UserService userService;

    public LoginController(final UserService userService) {
        this.userService = userService;
    }

    // @RequestMapping(/login?account=gugu&password=password)
    public User login(final String account, final String password) {
        return userService.login(account, password);
    }
}
