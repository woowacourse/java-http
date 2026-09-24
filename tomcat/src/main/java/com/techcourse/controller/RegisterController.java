package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(
            final HttpRequest request,
            final HttpResponse response
    ) {
        final String account = request.getBody()
                .getParameter("account");

        final String password = request.getBody()
                .getParameter("password");

        final String email = request.getBody()
                .getParameter("email");

        final User user = new User(
                account,
                password,
                email
        );

        InMemoryUserRepository.save(user);

        response.sendRedirect("/index.html");
    }
}
