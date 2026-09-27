package org.apache.coyote.http11;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public void add(Session session) {
        sessions.put(session.getId(), session);
    }

    public Session createSession() {
        Session session = new Session(UUID.randomUUID().toString());
        add(session);
        return session;
    }

    public Session findSession(String id) {
        return sessions.get(id);
    }

    public void remove(Session session) {
        sessions.remove(session.getId());
    }
}
