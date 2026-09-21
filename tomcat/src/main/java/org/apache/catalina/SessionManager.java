package org.apache.catalina;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();
    private static final SessionManager INSTANCE = new SessionManager();

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public Session createSession() {
        final var session = new Session(UUID.randomUUID().toString(), this);
        add(session);
        return session;
    }

    @Override
    public void add(final HttpSession session) {
        final var requiredSession = Objects.requireNonNull(session);
        if (!(requiredSession instanceof Session managedSession)) {
            throw new IllegalArgumentException("Unsupported session type: " + requiredSession.getClass().getName());
        }
        SESSIONS.put(managedSession.getId(), managedSession);
    }

    @Override
    public Session findSession(final String id) {
        final var session = SESSIONS.get(id);
        if (session != null) {
            session.access();
        }
        return session;
    }

    @Override
    public void remove(final HttpSession session) {
        SESSIONS.remove(session.getId());
    }

    private SessionManager() {
    }
}
