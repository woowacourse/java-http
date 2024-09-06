package com.techcourse.controller;

import com.techcourse.service.UserService;
import org.apache.coyote.http11.domain.controller.AbstractController;
import org.apache.coyote.http11.domain.request.HttpRequest;
import org.apache.coyote.http11.domain.request.RequestBody;
import org.apache.coyote.http11.domain.response.HttpResponse;

public class RegisterController extends AbstractController {

    private final UserService userService;


    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        return HttpResponse.redirect("/register.html").build();
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        RequestBody requestBody = request.getRequestBody();

        String account = requestBody.get("account");
        String email = requestBody.get("email");
        String password = requestBody.get("password");

        if (account == null || email == null || password == null) {
            return HttpResponse.redirect("/register.html").build();
        }

        userService.register(account, email, password);

        return HttpResponse.redirect("/index.html").build();
    }

}
