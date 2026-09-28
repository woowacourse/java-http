package org.apache.catalina;

import jakarta.servlet.http.HttpSession;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SessionManager implements Manager {

    private static final SessionManager INSTANCE = new SessionManager();
    private static final Map<String, HttpSession> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public Session createSession() {
        final var session = new Session(UUID.randomUUID().toString());
        add(session);
        return session;
    }

    @Override
    public void add(final HttpSession session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public HttpSession findSession(final String id) {
        final var session = SESSIONS.get(id);
        if (session instanceof Session) {
            ((Session) session).access();
        }
        return session;
    }

    @Override
    public void remove(final HttpSession session) {
        SESSIONS.remove(session.getId());
    }
}
