package org.apache.coyote.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public static void add(Session session) {
        SESSIONS.put(session.id(), session);
    }

    public static Session findSession(String id) {
        return SESSIONS.get(id);
    }
}
