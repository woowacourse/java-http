package org.apache.coyote.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static void add(final Session session) {
        sessions.put(session.getId(), session);
    }

    public static Session findSession(final String id) {
        if (id == null) {
            return null;
        }
        return sessions.get(id);
    }

    public static void remove(final String id) {
        sessions.remove(id);
    }
}
