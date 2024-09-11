package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();
    private static final SessionManager instance = new SessionManager();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return instance;
    }

    public Session findSession(String id) {
        return SESSIONS.get(id);
    }

    public void removeSession(String id) {
        SESSIONS.remove(id);
    }

    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }
}
