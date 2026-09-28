package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import java.io.IOException;

public class LoginController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        final var session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect("/index.html");
            return;
        }
        response.forward("/login.html");
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final var account = request.getParameter("account");
        final var password = request.getParameter("password");
        if (account == null || password == null) {
            response.sendRedirect("/401.html");
            return;
        }

        final var foundUser = InMemoryUserRepository.findByAccount(account);
        if (foundUser.isPresent()) {
            final var user = foundUser.get();
            if (user.checkPassword(password)) {
                request.getSession(true).setAttribute("user", user);
                response.sendRedirect("/index.html");
                return;
            }
        }
        response.sendRedirect("/401.html");
    }
}
