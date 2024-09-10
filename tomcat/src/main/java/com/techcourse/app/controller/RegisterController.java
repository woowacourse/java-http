package com.techcourse.app.controller;

import com.techcourse.app.service.UserService;
import com.techcourse.framework.handler.AbstractController;
import org.apache.coyote.http11.protocol.request.HttpRequest;
import org.apache.coyote.http11.protocol.response.HttpResponse;

public class RegisterController extends AbstractController {

    private final UserService userService;


    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setRedirect("/register.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getParameter("account");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (account == null || email == null || password == null) {
            response.setRedirect("/register.html");
            return;
        }

        userService.register(account, password, email);

        response.setRedirect("/index.html");
    }

}
