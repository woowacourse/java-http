package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public final class LoginController extends AbstractController {

    private static final String USER_SESSION_KEY = "user";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final var session = request.getSession(false);
        if (session != null && session.getAttribute(USER_SESSION_KEY) != null) {
            response.sendRedirect("/index.html");
            return;
        }

        response.sendStaticResource("/login.html");
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final var account = request.getParameter("account");
        final var password = request.getParameter("password");
        final var user = InMemoryUserRepository.findByAccount(account)
                .filter(foundUser -> foundUser.checkPassword(password));

        if (user.isPresent()) {
            request.getSession(true).setAttribute(USER_SESSION_KEY, user.get());
            response.sendRedirect("/index.html");
            return;
        }

        response.sendRedirect("/401.html");
    }
}
