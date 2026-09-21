package com.techcourse.controller;

import com.techcourse.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;

import java.util.Optional;
import java.util.UUID;

final class UserSessionService {

    static final String SESSION_USER = "user";

    private final SessionManager sessionManager = SessionManager.getInstance();

    Optional<User> findUser(String sessionId) {
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

    Session getOrCreate(String sessionId) {
        Session session = sessionManager.findSession(sessionId);
        if (session != null) {
            return session;
        }

        Session newSession = new Session(UUID.randomUUID().toString());
        sessionManager.add(newSession);
        return newSession;
    }

    void invalidate(String sessionId) {
        Session session = sessionManager.findSession(sessionId);
        if (session != null) {
            session.invalidate();
        }
    }
}
