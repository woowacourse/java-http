package com.techcourse.apiController;

import com.techcourse.model.User;
import com.techcourse.service.UserService;

public class LoginApiController {

    private final UserService userService;

    public LoginApiController(final UserService userService) {
        this.userService = userService;
    }

    // @RequestMapping(/login?account=gugu&password=password)
    public User login(final String account, final String password) {
        return userService.login(account, password);
    }
}
