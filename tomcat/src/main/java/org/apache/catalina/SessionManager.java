package org.apache.catalina;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @Override
    public void add(final Session session) {
        String sessionId = session.getId();
        sessions.putIfAbsent(sessionId, session);
    }

    @Override
    public Session findSession(final String id) {
        return sessions.get(id);
    }

    @Override
    public void remove(final Session session) {

    }

    public SessionManager() {
    }
}
