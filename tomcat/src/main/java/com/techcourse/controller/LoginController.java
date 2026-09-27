package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends AbstractController {

    private static final String USER = "user";

    @Override
    protected void doPost(
            final HttpRequest request,
            final HttpResponse response
    ) {
        final String account = request.getBody()
                .getParameter("account");

        final String password = request.getBody()
                .getParameter("password");

        final Optional<User> user = InMemoryUserRepository
                .findByAccount(account)
                .filter(it -> it.checkPassword(password));

        if (user.isPresent()) {
            request.getSession()
                    .setAttribute(USER, user.get());

            response.sendRedirect("/index.html");
            return;
        }

        response.sendRedirect("/401.html");
    }

    @Override
    protected void doGet(
            final HttpRequest request,
            final HttpResponse response
    ) {
        final Object user = request.getSession()
                .getAttribute(USER);

        if (user != null) {
            response.sendRedirect("/index.html");
            return;
        }

        response.setResourcePath("/login.html");
    }
}