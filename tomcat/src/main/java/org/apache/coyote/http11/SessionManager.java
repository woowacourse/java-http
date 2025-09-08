package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private SessionManager() {
    }

    public static Session createSession(String id) {
        Session session = new Session(id);
        SESSIONS.put(id, session);
        return session;
    }

    public static Session getSession(String id) {
        return SESSIONS.get(id);
    }

    public static Session findSession(String id) {
        Session session = SESSIONS.get(id);
        if (session == null) {
            session = createSession(id);
        }
        return session;
    }

    public static void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }
}
