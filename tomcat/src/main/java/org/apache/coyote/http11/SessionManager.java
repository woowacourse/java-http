package org.apache.coyote.http11;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public static void add(Session session) {
        sessions.put(session.getId(), session);
    }

    public static Session findSession(String id) {
        return sessions.get(id);
    }

    public static void remove(String id) {
        sessions.remove(id);
    }

    private SessionManager() {
    }
}
