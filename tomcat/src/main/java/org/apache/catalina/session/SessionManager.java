package org.apache.catalina.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public static void addSession(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Session findSession(final String sessionId) {
        return SESSIONS.get(sessionId);
    }

    public static void removeSession(final String sessionId) {
        SESSIONS.remove(sessionId);
    }
}
