package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class SessionManager implements Manager {

    private static final Map<String, StandardSession> SESSIONS = new HashMap<>();
    private static final SessionManager INSTANCE = new SessionManager();

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    @Override
    public Session createSession() {
        final var session = new StandardSession(UUID.randomUUID().toString(), this);
        add(session);
        return session;
    }

    @Override
    public void add(final Session session) {
        final var requiredSession = Objects.requireNonNull(session);
        if (!(requiredSession instanceof StandardSession managedSession)) {
            throw new IllegalArgumentException("Unsupported session type: " + requiredSession.getClass().getName());
        }
        SESSIONS.put(managedSession.getId(), managedSession);
    }

    @Override
    public Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }

    private SessionManager() {
    }
}
