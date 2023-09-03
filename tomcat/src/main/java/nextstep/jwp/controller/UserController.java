package nextstep.jwp.controller;

import nextstep.jwp.service.UserService;

import java.util.Map;

public class UserController {
    private final UserService userService = new UserService();

    public void loginlogin(final Map<String, String> totalQuery) {
        userService.login(totalQuery.get("account"), totalQuery.get("password"));
    }
}
