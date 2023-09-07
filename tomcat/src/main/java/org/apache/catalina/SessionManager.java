package org.apache.catalina;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private SessionManager() {
    }

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public static void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Session findSession(final String id) {
        return SESSIONS.get(id);
    }

}
