package com.techcourse.controller;

import com.techcourse.service.UserService;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.ContentType;
import org.apache.catalina.response.HttpResponse;
import org.apache.catalina.response.Status;

public class RegisterController extends MappingController {

    private final UserService userService;

    public RegisterController() {
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String body = resourceResolver.resolve(request.getUri());
        response.setStatusLine(Status.OK);
        response.setContentType(ContentType.of(request.getUri()));
        response.setBody(body);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        userService.register(request);

        String path = "/index.html";
        String body = resourceResolver.resolve(path);
        response.setStatusLine(Status.FOUND);
        response.setLocation(path);
        response.setContentType(ContentType.of(path));
        response.setBody(body);
    }
}
