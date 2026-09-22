package org.apache.catalina;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final SessionManager INSTANCE = new SessionManager();

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public Session create() {
        final Session session = new Session(UUID.randomUUID().toString());
        sessions.put(session.getId(), session);
        return session;
    }

    public Optional<Session> findSession(final String id) {
        return Optional.ofNullable(sessions.get(id));
    }

    public void remove(final String id) {
        sessions.remove(id);
    }
}
