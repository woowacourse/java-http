package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public static void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Session findSession(final String id) {
        if (!SESSIONS.containsKey(id)) {
            final Session session = new Session(id);
            SESSIONS.put(id, session);
            return session;
        }
        return SESSIONS.get(id);
    }

    public static void remove(final String id) {
        SESSIONS.remove(id);
    }

    private SessionManager() {
    }
}
