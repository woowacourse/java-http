package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public static Session getSession(final String sessionId) {
        return SESSIONS.get(sessionId);
    }

    public static boolean hasSession(final String sessionId) {
        return SESSIONS.containsKey(sessionId);
    }

    public static Session createSession() {
        final Session session = new Session(UUID.randomUUID().toString());
        SESSIONS.put(session.getId(), session);
        return session;
    }
}
