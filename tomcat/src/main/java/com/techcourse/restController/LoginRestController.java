package com.techcourse.restController;

import com.techcourse.model.User;
import com.techcourse.service.UserService;

public class LoginRestController {

    private final UserService userService;

    public LoginRestController(final UserService userService) {
        this.userService = userService;
    }

    // @RequestMapping(/login?account=gugu&password=password)
    public User login(final String account, final String password) {
        return userService.login(account, password);
    }
}
