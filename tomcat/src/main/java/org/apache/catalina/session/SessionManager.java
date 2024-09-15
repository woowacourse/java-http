package org.apache.catalina.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private static final SessionManager SESSION_MANAGER = new SessionManager();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return SESSION_MANAGER;
    }

    @Override
    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(String sessionId) {
        return SESSIONS.get(sessionId);
    }
}
