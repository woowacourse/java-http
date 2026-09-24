package org.apache.catalina.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public Session createSession() {
        Session session = new Session(UUID.randomUUID().toString());
        add(session);

        return session;
    }

    @Override
    public void add(final Session session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        if (id == null) {
            return null;
        }

        return sessions.get(id);
    }

    @Override
    public void remove(final Session session) {
        if (sessions.remove(session.getId(), session)) {
            session.invalidate();
        }
    }
}
