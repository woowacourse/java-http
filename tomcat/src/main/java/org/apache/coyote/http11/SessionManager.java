package org.apache.coyote.http11;


import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public static void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Session findSession(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        return SESSIONS.get(id);
    }

    public static Session createSession() {
        final String sessionId = UUID.randomUUID().toString();
        final Session session = new Session(sessionId);
        add(session);
        return session;
    }

    public static void remove(Session session) {
        SESSIONS.remove(session);
    }

    private SessionManager() {}
}
