package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();
    private static final SessionManager INSTANCE = new SessionManager();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (INSTANCE == null) {
            return new SessionManager();
        }
        return INSTANCE;
    }

    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public Session findSession(String id) {
        return SESSIONS.getOrDefault(id, Session.empty());
    }
}
