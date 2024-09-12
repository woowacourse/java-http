package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public SessionManager() {
    }

    public Session findSession(String id) {
        return SESSIONS.get(id);
    }

    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public void remove(Session session) {
        SESSIONS.remove(session.getId());
    }
}
