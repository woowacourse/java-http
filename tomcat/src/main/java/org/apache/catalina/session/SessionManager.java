package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private static final SessionManager SESSION_MANAGER = new SessionManager(new HashMap<>());

    private Map<String, Session> sessions;

    private SessionManager(Map<String, Session> sessions) {
        this.sessions = sessions;
    }

    public static SessionManager getInstance() {
        return SESSION_MANAGER;
    }

    public Session getSession(String sessionId) {
        return sessions.getOrDefault(sessionId, new Session());
    }

    public void setSession(String sessionId, Session session) {
        sessions.put(sessionId, session);
    }
}
