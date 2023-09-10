package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.session.Session;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public static void add(Session session) {
        SESSIONS.put(session.id(), session);
    }

    public static Session findSession(String id) {
        return SESSIONS.get(id);
    }

    public void remove(Session session) {
        SESSIONS.remove(session.id());
    }
}
