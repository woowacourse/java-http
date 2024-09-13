package org.apache.catalina.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public SessionManager() {
    }

    public Session findSession(String id) {
        return SESSIONS.get(id);
    }

    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }
}
