package org.apache.coyote.status;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    // static!
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public static void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    public static void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }
}
