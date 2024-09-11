package org.apache.catalina.manager;

import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();
    private static final SessionManager instance = new SessionManager();

    public static SessionManager getInstance() {
        return instance;
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
    public void remove(Session session) {
        SESSIONS.remove(session.getId());
    }

    @Override
    public void remove(final String id) {
        SESSIONS.remove(id);
    }

    private SessionManager() {
    }
}
