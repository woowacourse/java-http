package org.apache.coyote.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public static void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Session findSession(final String id) {
        return id == null ? null : SESSIONS.get(id);
    }

    public static void remove(final String id) {
        if (id != null) {
            SESSIONS.remove(id);
        }
    }

    private SessionManager() {
    }
}
