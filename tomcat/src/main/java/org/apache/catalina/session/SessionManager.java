package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final Map<String, Session> sessions = new HashMap<>();

    private static SessionManager INSTANCE;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SessionManager();
        }
        return INSTANCE;
    }

    @Override
    public void add(Session session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public Session findSession(String id) {
        return sessions.get(id);
    }

    @Override
    public void remove(Session session) {
        sessions.remove(session.getId());
    }

    @Override
    public Session createSession(String sessionId) {
        return new Session(sessionId, this);
    }

    @Override
    public void clear() {
        sessions.clear();
    }
}
