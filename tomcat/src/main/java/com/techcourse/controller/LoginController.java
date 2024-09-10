package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import org.apache.controller.AbstractController;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.session.Session;

public class LoginController extends AbstractController {

    public static final String SESSION_USER_KEY = "user";

    public LoginController() {
        super("/login");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getQueryParamFromBody("account");
        String password = request.getQueryParamFromBody("password");

        InMemoryUserRepository.findByAccount(account)
                .ifPresentOrElse(
                        user -> {
                            if (user.checkPassword(password)) {
                                acceptLogin(request, response, user);
                                return;
                            }
                            response.sendRedirect("/401.html");
                        },
                        () -> response.sendRedirect("/401.html")
                );
    }

    private void acceptLogin(HttpRequest request, HttpResponse response, User user) {
        Session session = request.getSession();
        session.setAttribute(SESSION_USER_KEY, user);
        response.addCookie(Session.SESSION_COOKIE_KEY, session.getId());
        response.sendRedirect("/index.html");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        if (hasUser(request)) {
            response.sendRedirect("/index.html");
            return;
        }
        response.addStaticBody("/login.html");
    }

    private boolean hasUser(HttpRequest request) {
        Session session = request.getSession();
        return session.hasAttribute(SESSION_USER_KEY);
    }
}
