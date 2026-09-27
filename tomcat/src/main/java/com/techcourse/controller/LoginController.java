package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;
import java.util.Optional;

public class LoginController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        if (request.getSession().getAttribute("user") != null) {
            redirect(response, "/index.html");
            return;
        }

        renderStaticResource(response, "/login.html");
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final var authenticatedUser = findAuthenticatedUser(request);

        if (authenticatedUser.isEmpty()) {
            redirect(response, "/401.html");
            return;
        }

        request.getSession().setAttribute("user", authenticatedUser.get());
        redirect(response, "/index.html");
    }

    private Optional<User> findAuthenticatedUser(final HttpRequest request) {
        final var account = request.getParameter("account");
        final var password = request.getParameter("password");

        if (account == null || password == null) {
            return Optional.empty();
        }

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }
}
