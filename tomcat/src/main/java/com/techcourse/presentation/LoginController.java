package com.techcourse.presentation;

import com.techcourse.application.LoginService;
import java.util.Map;

public class LoginController {

    private final LoginService loginService;

    public LoginController(final LoginService loginService) {
        this.loginService = loginService;
    }

    public String login(Map<String, String> params) {
        if (params.size() != 2 || !params.containsKey("account") || !params.containsKey("password")) {
            throw new IllegalStateException("적절하지 않은 로그인 요청입니다.");
        }

        loginService.login(params.get("account"), params.get("password"));

        return "/login.html";
    }
}
