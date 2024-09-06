package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final SessionManager INSTANCE = new SessionManager();

    private final Map<String, Session> sessions = new HashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public boolean hasSession(String sessionId) {
        return sessions.keySet().contains(sessionId);
    }

    public void putSession(String sessionId, Session session) {
        sessions.put(sessionId, session);
    }

    public void invalidateSession(String sessionId) {
        sessions.remove(sessionId);
    }
}
