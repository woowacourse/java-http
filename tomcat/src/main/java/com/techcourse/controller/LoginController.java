package com.techcourse.controller;

import com.techcourse.service.UserService;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.ContentType;
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
            String path = "/index.html";
            String body = resourceResolver.resolve(path);
            response.setStatusLine(Status.UNAUTHORIZED);
            response.setLocation(path);
            response.setContentType(ContentType.of(path));
            response.setContentLength(body.getBytes().length);
            response.setBody(body);
            return;
        }
        String path = "/login.html";
        String body = resourceResolver.resolve(path);
        response.setStatusLine(Status.OK);
        response.setContentType(ContentType.of(path));
        response.setContentLength(body.getBytes().length);
        response.setBody(body);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        boolean successLogin = userService.login(request, response);
        if (successLogin) {
            String path = "/index.html";
            String body = resourceResolver.resolve(path);
            response.setStatusLine(Status.FOUND);
            response.setLocation(path);
            response.setContentType(ContentType.of(path));
            response.setContentLength(body.getBytes().length);
            response.setBody(body);
            return;
        }
        String path = "/401.html";
        String body = resourceResolver.resolve(path);
        response.setStatusLine(Status.UNAUTHORIZED);
        response.setLocation(path);
        response.setContentType(ContentType.of(path));
        response.setContentLength(body.getBytes().length);
        response.setBody(body);
    }
}
