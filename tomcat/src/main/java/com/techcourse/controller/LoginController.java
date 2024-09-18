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
        if (isLoggedIn(request, response)) {
            response.setRedirectResponse("/index.html");
            return;
        }

        response.setStaticResourceResponse("/login.html");
    }

    private boolean isLoggedIn(HttpRequest request, HttpResponse response) throws IOException {
        Session session = request.getSession(false);

        return session != null && session.getAttribute(SESSION_USER_ATTRIBUTE) != null;
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String account = request.findBodyValueByKey("account");
        String password = request.findBodyValueByKey("password");

        InMemoryUserRepository.findByAccount(account)
                .filter(it -> it.checkPassword(password))
                .ifPresentOrElse(user -> processLoginSuccess(request, response, user),
                        () -> response.setRedirectResponse("/401.html"));
    }

    private void processLoginSuccess(HttpRequest request, HttpResponse response, User user) {
        initializeSessionIfNotExists(request, response, user);
        response.setRedirectResponse("/index.html");
    }

    private void initializeSessionIfNotExists(HttpRequest request, HttpResponse response, User user) {
        if (request.checkSessionNotExists()) {
            Session session = request.getSession(true);
            session.setAttribute(SESSION_USER_ATTRIBUTE, user);
            response.setCookie(Cookie.createSessionCookie(session.getId()));
        }
    }
}
