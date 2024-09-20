package org.apache.catalina;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static void add(Session session) {
        sessions.put(session.getId(), session);
    }

    public static Session findSession(String id) {
        return sessions.get(id);
    }

    public static void remove(Session session) {
        sessions.remove(session.getId());
    }
}
