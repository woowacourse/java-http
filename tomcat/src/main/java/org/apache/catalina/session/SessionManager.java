package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final SessionManager INSTANCE = new SessionManager();
    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private SessionManager() {
    }

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
}
