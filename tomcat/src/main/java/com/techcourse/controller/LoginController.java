package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class LoginController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        final Session session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect("/index.html");
            return;
        }
        response.forward("/login.html");
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws IOException {
        final Map<String, String> params = request.getBodyParams();
        final Optional<User> user = InMemoryUserRepository.findByAccount(params.get("account"));
        if (user.isEmpty() || !user.get().checkPassword(params.get("password"))) {
            response.sendRedirect("/401.html");
            return;
        }

        request.getSession(true).setAttribute("user", user.get());
        response.sendRedirect("/index.html");
    }
}
