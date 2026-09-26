package com.techcourse.controller;

import com.techcourse.service.UserService;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RegisterController extends AbstractController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setStaticResource(request.getPath());
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        userService.register(
                request.getParameter("account"),
                request.getParameter("password"),
                request.getParameter("email")
        );
        response.sendRedirect("/index.html");
    }
}
