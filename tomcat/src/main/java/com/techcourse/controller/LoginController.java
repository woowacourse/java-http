package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.Session;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StaticResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final StaticResourceHandler staticResourceHandler = new StaticResourceHandler();

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        final Session session = request.getSession(false);

        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect("/index.html");
            return;
        }

        staticResourceHandler.serve("static/login.html", response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final String account = request.getParameter("account");
        final String password = request.getParameter("password");

        if (account == null || password == null) {
            response.sendRedirect("/401.html");
            return;
        }

        final Optional<User> loginUser = InMemoryUserRepository
                .findByAccount(account)
                .filter(user -> user.checkPassword(password));

        if (loginUser.isEmpty()) {
            response.sendRedirect("/401.html");
            return;
        }

        final User user = loginUser.get();
        request.getSession(true).setAttribute("user", user);
        log.info("user : {}", user);
        response.sendRedirect("/index.html");
    }
}
