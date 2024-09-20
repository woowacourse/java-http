package org.apache.catalina.session;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public SessionManager() {
    }

    public Optional<Session> findSession(String id) {
        Session session = SESSIONS.get(id);
        return Optional.ofNullable(session);
    }

    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }
}
