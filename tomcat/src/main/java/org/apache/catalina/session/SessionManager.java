package org.apache.catalina.session;

import org.apache.catalina.Manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public static final SessionManager INSTANCE = new SessionManager();

    private SessionManager() {
    }

    public Session createSession() {
        final Session session = new Session(UUID.randomUUID().toString());
        add(session);
        return session;
    }

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        if (id == null) {
            return null;
        }
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final String id) {
        SESSIONS.remove(id);
    }

    public void invalidate(final String id) {
        final Session session = SESSIONS.remove(id);
        if (session != null) {
            session.clear();
        }
    }
}
