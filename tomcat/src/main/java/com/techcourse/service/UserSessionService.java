package com.techcourse.service;

import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import java.util.Optional;

public final class UserSessionService {

    public static final String SESSION_USER = "user";

    private final SessionManager sessionManager = SessionManager.getInstance();

    public Optional<User> findUser(HttpRequest request) {
        Session session = sessionManager.findSession(request);
        if (session == null) {
            return Optional.empty();
        }

        Object user = session.getAttribute(SESSION_USER);
        if (user instanceof User loginUser) {
            return Optional.of(loginUser);
        }
        return Optional.empty();
    }

    public void startAuthenticatedSession(HttpRequest request, HttpResponse response, User user) {
        Session newSession = sessionManager.replaceSession(request, response);
        newSession.setAttribute(SESSION_USER, user);
    }

    public void invalidate(HttpRequest request, HttpResponse response) {
        sessionManager.invalidate(request, response);
    }
}
