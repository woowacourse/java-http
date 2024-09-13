package com.techcourse.controller;

import com.techcourse.controller.dto.RegisterRequest;
import com.techcourse.service.UserService;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.util.StaticResourceReader;
import org.apache.coyote.http11.common.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseFile;

public class RegisterController extends AbstractController {

    private static final ResponseFile registerPage = StaticResourceReader.read("/register.html");

    private final UserService userService;

    public RegisterController() {
        this.userService = UserService.getInstance();
    }

    @Override
    public String matchedPath() {
        return "/register";
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatusCode.OK)
                .setBody(registerPage);
    }

    @Override
    public void doPost(HttpRequest httpRequest, HttpResponse response) {
        RegisterRequest registerRequest = RegisterRequest.of(httpRequest.getBody());
        userService.register(registerRequest);
        response.sendRedirect("/index.html");
    }
}
