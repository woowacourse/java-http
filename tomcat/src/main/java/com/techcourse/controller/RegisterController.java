package com.techcourse.controller;

import com.techcourse.service.UserService;
import org.apache.catalina.controller.AbstractApiController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseConfigurator;

public class RegisterController extends AbstractApiController {

    private final UserService userService;

    public RegisterController() {
        super("/register");
        this.userService = new UserService();
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final String account = request.getBodyParameter("account");
        final String password = request.getBodyParameter("password");
        final String email = request.getBodyParameter("email");
        userService.signup(account, password, email);

        HttpResponseConfigurator.redirect(response, "/index.html");
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        HttpResponseConfigurator.okWithStaticResource(response, "/register.html");
    }
}
