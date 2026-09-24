package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;

public class SessionManager implements Manager {

    private static final SessionManager INSTANCE = new SessionManager();
    private final Map<String, Session> sessions = new HashMap<>();

    private SessionManager() {
    }

    @Override
    public void add(final Session session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        return sessions.get(id);
    }

    @Override
    public void remove(final Session session) {
        sessions.remove(session.getId());
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }
}
