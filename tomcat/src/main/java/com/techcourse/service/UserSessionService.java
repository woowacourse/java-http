package com.techcourse.service;

import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;

import java.util.Optional;
import java.util.UUID;

public final class UserSessionService {

    public static final String SESSION_USER = "user";

    private final SessionManager sessionManager = SessionManager.getInstance();

    public Optional<User> findUser(String sessionId) {
        Session session = sessionManager.findSession(sessionId);
        if (session == null) {
            return Optional.empty();
        }

        Object user = session.getAttribute(SESSION_USER);
        if (user instanceof User loginUser) {
            return Optional.of(loginUser);
        }
        return Optional.empty();
    }

    public Session startAuthenticatedSession(String previousSessionId, User user) {
        if (previousSessionId != null) {
            Session previousSession = sessionManager.findSession(previousSessionId);
            if (previousSession != null) {
                previousSession.invalidate();
            }
        }

        Session newSession = new Session(UUID.randomUUID().toString());
        newSession.setAttribute(SESSION_USER, user);
        sessionManager.add(newSession);
        return newSession;
    }

    public void invalidate(String sessionId) {
        Session session = sessionManager.findSession(sessionId);
        if (session != null) {
            session.invalidate();
        }
    }
}
