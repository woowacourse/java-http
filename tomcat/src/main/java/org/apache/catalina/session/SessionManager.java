package org.apache.catalina.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public void add(Session session) {
        sessions.put(session.getId(), session);
    }

    public Session findSession(String id) {
        return sessions.get(id);
    }

    public void remove(Session session) {
        sessions.remove(session.getId());
    }
}
