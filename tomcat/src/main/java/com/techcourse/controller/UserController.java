package com.techcourse.controller;

import java.util.Map;

import com.techcourse.exception.DashboardException;
import com.techcourse.model.User;
import com.techcourse.service.UserService;

public class UserController {
    private final UserService userService = new UserService();

    public User login(Map<String, String> queryParams) {
        String account = queryParams.get("account");
        String password = queryParams.get("password");

        if (account == null || password == null) {
            throw new DashboardException("Values for authorization is missing.");
        }

        User user = userService.login(account, password);

        return user;
    }
}

