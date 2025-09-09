package org.apache.coyote.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public void add(Session session) {
        if (session == null || session.getId() == null) {
            throw new IllegalArgumentException("Session cannot be null");
        }
        sessions.put(session.getId(), session);
    }

    public Session findSession(String id) {
        if (id == null) {
            return null;
        }
        return sessions.get(id);
    }

    public void remove(Session session) {
        if (session != null) {
            sessions.remove(session.getId());
        }
    }

    public int getSessionCount() {
        return sessions.size();
    }
}
