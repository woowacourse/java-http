package com.techcourse.controller;

import com.techcourse.exception.AuthenticationException;
import com.techcourse.service.LoginService;
import org.apache.coyote.http11.domain.controller.AbstractController;
import org.apache.coyote.http11.domain.request.HttpRequest;
import org.apache.coyote.http11.domain.response.HttpResponse;

public class LoginController extends AbstractController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");

        if (account == null || password == null) {
            return HttpResponse.redirect("/login.html").build();
        }

        try {
            loginService.login(account, password);
        } catch (AuthenticationException e) {
            return HttpResponse.redirect("/401.html").build();
        }

        return HttpResponse.redirect("/index.html").build();
    }
}
