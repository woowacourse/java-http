package com.techcourse.controller;

import com.techcourse.service.RegisterService;
import org.apache.coyote.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    private final RegisterService registerService = RegisterService.getInstance();

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        super.service(request, response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        String email = request.getParameter("email");

        registerService.register(account, password, email);

        response.redirect("/index.html");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.ok("register.html");
    }
}
