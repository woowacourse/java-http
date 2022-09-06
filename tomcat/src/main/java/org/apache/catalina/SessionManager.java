package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public static void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static boolean isValid(String sessionId) {
        return SESSIONS.keySet()
                .stream()
                .anyMatch(sessionId::equals);
    }
}
