package org.apache.coyote.http11.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public static void add(final Session session) {
        sessions.put(session.getId(), session);
    }

    public static Session findSession(final String id) {
        return sessions.get(id);
    }

    public static void remove(final String id) {
        final Session session = sessions.get(id);
        session.invalidate();
        sessions.remove(id);
    }

    private SessionManager() {
    }
}
