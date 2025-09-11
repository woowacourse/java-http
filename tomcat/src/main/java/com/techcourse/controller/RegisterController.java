package com.techcourse.controller;

import com.techcourse.service.RegisterService;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

public class RegisterController extends AbstractController {

    private final RegisterService registerService;

    public RegisterController(final RegisterService registerService) {
        this.registerService = registerService;
    }

    @Override
    public String providableUrl() {
        return "/register";
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.setOk("register.html");
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final String account = request.getBodyElement("account");
        final String password = request.getBodyElement("password");
        final String email = request.getBodyElement("email");

        registerService.register(account, password, email);

        response.setFound("index.html");
    }
}
