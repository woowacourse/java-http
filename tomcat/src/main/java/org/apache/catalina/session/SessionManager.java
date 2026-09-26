package org.apache.catalina.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public SessionManager() {
    }

    public Session createSession() {
        final Session session = new Session(UUID.randomUUID().toString());
        add(session);
        return session;
    }

    public void add(final Session session) {
        sessions.put(session.getId(), session);
    }

    public Session findSession(final String id) {
        if (id == null) {
            return null;
        }
        return sessions.get(id);
    }
}
