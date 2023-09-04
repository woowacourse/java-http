package org.apache.catalina.manager;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.Session;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private SessionManager() {
    }

    public static void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static void remove(Session session) {
        SESSIONS.remove(session.getId());
    }

    public static Session findSession(String id) {
        return SESSIONS.get(id);
    }
}
