package org.apache.coyote.http11.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public static Session createSession() {
        final Session session = new Session(UUID.randomUUID().toString());
        add(session);
        return session;
    }

    public static Session findOrCreate(final String id) {
        final Session session = findSession(id);
        return session != null ? session : createSession();
    }

    public static Session findSession(final String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        return SESSIONS.get(id);
    }

    public static void add(final Session session) {
        SESSIONS.putIfAbsent(session.getId(), session);
    }

    public static void remove(final Session session) {
        SESSIONS.remove(session.getId(), session);
    }

    private SessionManager() {
    }
}
