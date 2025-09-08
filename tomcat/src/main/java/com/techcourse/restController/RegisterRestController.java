package com.techcourse.restController;

import com.techcourse.service.UserService;

public class RegisterRestController {

    private final UserService userService;

    public RegisterRestController(final UserService userService) {
        this.userService = userService;
    }

    // @RequestMapping(/register)
    public void register(String account, String password, String email){
        userService.register(account, password, email);
    }
}
