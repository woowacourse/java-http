package org.apache.coyote.http11;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SessionManager {
    private final ConcurrentMap<String, Session> sessions = new ConcurrentHashMap<>();

    public void addSession(String id, Session session) {
        sessions.put(id, session);
    }

    public Session getOrCreateSession(String id) {
        return sessions.computeIfAbsent(
                id,
                sessionId -> new Session(sessionId, new ConcurrentHashMap<>())
        );
    }

    public boolean containsSession(String id) {
        return sessions.containsKey(id);
    }

    public boolean isSessionContainsKey(String id, String key) {
        Session session = sessions.get(id);
        return session != null && session.getUser(key) != null;
    }

    public boolean containsSessionKey(String key) {
        return sessions.values().stream().anyMatch(session -> session.getUser(key) != null);
    }
}
