package org.apache.catalina.session;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final SessionManager INSTANCE = new SessionManager();

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public void add(final Session session) {
        sessions.put(session.getId(), session);
    }

    public Optional<Session> findSession(final String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(sessions.get(id));
    }

    public void remove(final Session session) {
        sessions.remove(session.getId());
    }
}
