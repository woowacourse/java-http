package org.apache.catalina.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

    private static final SessionManager INSTANCE = new SessionManager();
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        if (!SESSIONS.containsKey(id)) {
            throw new IllegalArgumentException("No Such Session exists");
        }
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final Session session) {
        if (!SESSIONS.containsKey(session.getId())) {
            throw new IllegalArgumentException("No Such Session exists");
        }
        SESSIONS.remove(session.getId());
    }

    public boolean hasSession(final Session session) {
        return SESSIONS.containsKey(session.getId());
    }

    private SessionManager() {
    }
}
