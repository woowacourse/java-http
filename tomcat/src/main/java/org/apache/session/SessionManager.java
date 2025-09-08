package org.apache.session;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public static void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Session findSession(String sessionId) {
        return SESSIONS.get(sessionId);
    }

    public static void remove(final String id) {
        SESSIONS.remove(id);
    }

    private SessionManager() {
    }
}
