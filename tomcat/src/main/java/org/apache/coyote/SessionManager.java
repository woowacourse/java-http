package org.apache.coyote;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static SessionManager sessionManager;
    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public static SessionManager getInstance() {
        if (sessionManager == null) {
            sessionManager = new SessionManager();
        }
        return sessionManager;
    }

    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public Session findSession(final String id) {
        return SESSIONS.getOrDefault(id, null);
    }

    public void remove(final String id) {
        SESSIONS.remove(id);
    }

    private SessionManager() {}
}
