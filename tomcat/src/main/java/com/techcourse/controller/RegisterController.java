package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public final class RegisterController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        response.sendStaticResource("/register.html");
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final var user = new User(
                request.getParameter("account"),
                request.getParameter("password"),
                request.getParameter("email")
        );

        InMemoryUserRepository.save(user);
        response.sendRedirect("/index.html");
    }
}
