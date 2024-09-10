package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.controller.AbstractController;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;

public class RegisterController extends AbstractController {

    public RegisterController() {
        super("/register");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String account = request.getQueryParamFromBody("account");
        String email = request.getQueryParamFromBody("email");
        String password = request.getQueryParamFromBody("password");

        User user = new User(account, password, email);

        InMemoryUserRepository.save(user);
        response.sendRedirect("/index.html");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.addStaticBody("/register.html");
    }
}
