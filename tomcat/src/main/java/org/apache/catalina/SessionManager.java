package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;

public class SessionManager implements Manager {

    private static final SessionManager INSTANCE = new SessionManager();

    private static final Map<String, Session> SESSIONS = new HashMap<>();


    public static SessionManager getInstance() {
        return INSTANCE;
    }

    private SessionManager() {
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
    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }
}
