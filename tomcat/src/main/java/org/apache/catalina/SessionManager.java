package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();
    private static final SessionManager INSTANCE = new SessionManager();

    private SessionManager() {}

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final String id) {
        SESSIONS.remove(id);
    }

    public Session createSession() {
        final String id = UUID.randomUUID().toString();
        final Session session = new Session(id);
        add(session);
        return session;
    }
}
