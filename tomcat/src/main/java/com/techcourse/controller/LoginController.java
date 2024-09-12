package com.techcourse.controller;

import com.techcourse.service.UserService;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;
import org.apache.catalina.response.Status;

public class LoginController extends MappingController {

    private final UserService userService;

    public LoginController() {
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        if (userService.existsUser(request)) {
            String body = resourceResolver.resolve("/index.html");
            response.setStatus(Status.UNAUTHORIZED);
            response.setLocation("/index.html");
            response.setContentType("text/html;charset=utf-8");
            response.setBody(body);
            return;
        }
        String body = resourceResolver.resolve("/login.html");
        response.setStatus(Status.OK);
        response.setContentType("text/html;charset=utf-8");
        response.setBody(body);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        boolean successLogin = userService.login(request, response);
        if (successLogin) {
            String body = resourceResolver.resolve("/index.html");
            response.setStatus(Status.FOUND);
            response.setLocation("/index.html");
            response.setContentType("text/html;charset=utf-8");
            response.setBody(body);
            return;
        }
        String body = resourceResolver.resolve("/401.html");
        response.setStatus(Status.UNAUTHORIZED);
        response.setLocation("/401.html");
        response.setContentType("text/html;charset=utf-8");
        response.setBody(body);
    }
}
