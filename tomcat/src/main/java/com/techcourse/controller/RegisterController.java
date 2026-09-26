package com.techcourse.controller;

import com.techcourse.service.UserService;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.resource.StaticResource;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    private static final String REGISTER_PAGE = "/register.html";

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        StaticResource.from(REGISTER_PAGE).writeTo(response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        userService.register(
                request.getParameter("account"),
                request.getParameter("password"),
                request.getParameter("email")
        );
        response.redirect("/index.html");
    }
}
