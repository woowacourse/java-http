package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private SessionManager() {
    }

    public static Session getSession(String id) {
        return SESSIONS.get(id);
    }

    public static void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }
}
