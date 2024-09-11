package com.techcourse.controller;

import java.io.IOException;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.Session;

public class LoginController extends AbstractController {

    private static final String SESSION_USER_ATTRIBUTE = "user";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (isLoggedIn(request, response)) return;

        response.sendStaticResourceResponse("/login.html");
    }

    private boolean isLoggedIn(HttpRequest request, HttpResponse response) throws IOException {
        Session session = request.getSession(false);

        if (session == null || session.getAttribute(SESSION_USER_ATTRIBUTE) == null) {
            return false;
        }
        response.sendRedirect("/index.html");
        return true;
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String account = request.findBodyValueByKey("account");
        String password = request.findBodyValueByKey("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .filter(it -> it.checkPassword(password))
                .orElse(null);

        if (user == null) {
            response.sendRedirect("/401.html");
            return;
        }

        initializeSessionIfNotExists(request, response, user);

        response.sendRedirect("/index.html");
    }

    private void initializeSessionIfNotExists(HttpRequest request, HttpResponse response, User user) {
        if (request.sessionNotExists()) {
            Session session = request.getSession(true);
            session.setAttribute(SESSION_USER_ATTRIBUTE, user);
            response.setCookie(Cookie.createSessionCookie(session.getId()));
        }
    }
}
