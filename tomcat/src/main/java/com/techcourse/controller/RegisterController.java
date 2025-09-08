package com.techcourse.controller;

import com.techcourse.service.UserService;

public class RegisterController {

    private final UserService userService;

    public RegisterController(final UserService userService) {
        this.userService = userService;
    }

    // @RequestMapping(/register)
    public void register(String account, String password, String email){
        userService.register(account, password, email);
    }
}
