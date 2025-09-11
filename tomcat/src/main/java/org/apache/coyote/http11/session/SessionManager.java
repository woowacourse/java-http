package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {
    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private SessionManager() {
    }

    public static Session getOrCreateSession(final String sessionId, final boolean create) {
        Session session = findSession(sessionId);
        if (session != null) {
            return session;
        }

        if (create) {
            Session newSession = new Session(UUID.randomUUID().toString());
            add(newSession);
            return newSession;
        }
        return null;
    }

    private static void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    private static Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    public static void remove(final String id) {
        SESSIONS.remove(id);
    }
}
