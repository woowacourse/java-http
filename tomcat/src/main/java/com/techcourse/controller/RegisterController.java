package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import java.io.IOException;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        response.forward("/register.html");
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final var account = request.getParameter("account");
        final var password = request.getParameter("password");
        final var email = request.getParameter("email");
        if (account == null || account.isBlank() || password == null || password.isBlank()
                || email == null || email.isBlank()) {
            response.sendRedirect("/register");
            return;
        }
        final var user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        response.sendRedirect("/index.html");
    }
}
