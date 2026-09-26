package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final Optional<User> user = findLoginUser(request);
        if (user.isEmpty()) {
            response.redirect("/401.html");
            return;
        }
        final Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", user.get());
        SessionManager.INSTANCE.add(session);
        response.redirectWithCookie("/index.html", session.getId());
    }

    private Optional<User> findLoginUser(final HttpRequest request) {
        final String account = request.getParameter("account");
        final String password = request.getParameter("password");

        if (account == null) {
            return Optional.empty();
        }

        final Optional<User> user = InMemoryUserRepository.findByAccount(account)
                .filter(u -> u.checkPassword(password));
        user.ifPresent(u -> log.info("로그인 성공! user: {}", u));

        return user;
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.redirect("/index.html");
    }
}
