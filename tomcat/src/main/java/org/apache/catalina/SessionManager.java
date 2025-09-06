package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;

public class SessionManager implements Manager{

    private static final Map<String, Session> SESSIONS = new HashMap<>();
    private static final SessionManager instance = new SessionManager();

    @Override
    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(Session session) {
        SESSIONS.remove(session.getId());
    }

    private SessionManager() {}

    public static SessionManager getInstance() {
        return instance;
    }
}
