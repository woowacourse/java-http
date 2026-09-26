package com.techcourse.controller;

import com.techcourse.exception.AuthenticationException;
import com.techcourse.model.User;
import com.techcourse.service.UserService;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.resource.StaticResource;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends AbstractController {

    private static final String LOGIN_PAGE = "/login.html";

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (LoginSession.of(request.getSession()).isLoggedIn()) {
            response.redirect("/index.html");
            return;
        }
        StaticResource.from(LOGIN_PAGE).writeTo(response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        try {
            User user = userService.login(request.getParameter("account"), request.getParameter("password"));
            LoginSession.of(request.getSession()).login(user);
            response.redirect("/index.html");
        } catch (AuthenticationException e) {
            response.redirect("/401.html");
        }
    }
}
