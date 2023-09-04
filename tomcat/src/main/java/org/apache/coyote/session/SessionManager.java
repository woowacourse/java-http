package org.apache.coyote.session;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private SessionManager() {
    }

    public static void add(final Session session) {
        SESSIONS.put(session.id(), session);
    }

    public static Session findSession(final String id) {
        return SESSIONS.getOrDefault(id, null);
    }

    public void remove(final Session session) {
        SESSIONS.remove(session.id());
    }
}
