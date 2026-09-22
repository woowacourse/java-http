package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.sendStaticResource(request.getResourcePath());
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getParameter("account").orElse(null);
        String password = request.getParameter("password").orElse(null);
        String email = request.getParameter("email").orElse(null);

        try {
            InMemoryUserRepository.save(new User(account, password, email));
        } catch (IllegalArgumentException e) {
            response.redirect("/register.html");
            return;
        }

        response.redirect("/index.html");
    }
}
