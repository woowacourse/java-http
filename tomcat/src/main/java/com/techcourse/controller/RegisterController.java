package com.techcourse.controller;

import com.techcourse.service.UserService;
import org.apache.coyote.http11.domain.controller.AbstractController;
import org.apache.coyote.http11.domain.request.HttpRequest;
import org.apache.coyote.http11.domain.response.HttpResponse;

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
