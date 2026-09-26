package com.techcourse.controller;

import com.techcourse.model.User;
import org.apache.catalina.session.Session;

class LoginSession {

    private static final String USER = "user";

    private final Session session;

    private LoginSession(Session session) {
        this.session = session;
    }

    static LoginSession of(Session session) {
        return new LoginSession(session);
    }

    void login(User user) {
        session.setAttribute(USER, user);
    }

    boolean isLoggedIn() {
        return session.getAttribute(USER) != null;
    }
}
