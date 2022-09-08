package org.apache.coyote.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public static void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    public static boolean isValidateSession(final Session session) {
        return SESSIONS.containsKey(session.getId());
    }
}
