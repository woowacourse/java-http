package com.techcourse.web;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.Session;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import java.io.IOException;
import java.util.Optional;

public class LoginController extends AbstractController {

    private static final String USER_ATTRIBUTE_KEY = "user";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        Session session = request.getSession(false);
        if (session != null && session.getAttribute(USER_ATTRIBUTE_KEY) != null) {
            response.sendRedirect("/index.html");
            return;
        }
        response.sendStaticFile("/login.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        Optional<User> user = findAuthenticatedUser(account, password);
        if (user.isEmpty()) {
            response.sendRedirect("/401.html");
            return;
        }

        Session session = request.getSession(true);
        session.setAttribute(USER_ATTRIBUTE_KEY, user.get());
        response.sendRedirect("/index.html");
    }

    private Optional<User> findAuthenticatedUser(String account, String password) {
        if (account == null || password == null) {
            return Optional.empty();
        }

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty()) {
            return Optional.empty();
        }
        if (!user.get().checkPassword(password)) {
            return Optional.empty();
        }
        return user;
    }
}
