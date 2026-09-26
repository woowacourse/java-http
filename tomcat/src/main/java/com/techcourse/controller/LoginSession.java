package com.techcourse.controller;

import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;

class LoginSession {

    private static final String USER = "user";

    private final Session session;

    private LoginSession(Session session) {
        this.session = session;
    }

    static LoginSession of(HttpRequest request) {
        return new LoginSession(SessionManager.getInstance().findSession(request.getSessionId()));
    }

    void login(User user) {
        session.setAttribute(USER, user);
    }

    boolean isLoggedIn() {
        return session.getAttribute(USER) != null;
    }
}
