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
            response.setStatus(Status.UNAUTHORIZED);
            response.setLocation("/index.html");
            response.setContentType("text/html;charset=utf-8");
            response.setBodyUri("/index.html");
            return;
        }
        response.setStatus(Status.OK);
        response.setContentType("text/html;charset=utf-8");
        response.setBodyUri("/login.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        boolean successLogin = userService.login(request, response);
        if (successLogin) {
            response.setStatus(Status.FOUND);
            response.setLocation("/index.html");
            response.setContentType("text/html;charset=utf-8");
            response.setBodyUri("/index.html");
            return;
        }
        response.setStatus(Status.UNAUTHORIZED);
        response.setLocation("/401.html");
        response.setContentType("text/html;charset=utf-8");
        response.setBodyUri("/401.html");
    }
}
