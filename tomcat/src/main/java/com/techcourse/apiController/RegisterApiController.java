package com.techcourse.apiController;

import com.techcourse.service.UserService;

public class RegisterApiController {

    private final UserService userService;

    public RegisterApiController(final UserService userService) {
        this.userService = userService;
    }

    // @RequestMapping(/register)
    public void register(String account, String password, String email){
        userService.register(account, password, email);
    }
}
