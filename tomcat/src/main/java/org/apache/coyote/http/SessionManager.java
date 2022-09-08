package org.apache.coyote.http;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();


    public static Session add() {
        final Session session = new Session(UUID.randomUUID().toString());
        SESSIONS.put(session.getId() , session);
        return findSession(session.getId());
    }

    public static Session findSession(String id) {
        return SESSIONS.get(id);
    }

    public static void remove(Session session) {
        SESSIONS.remove(session);
    }

    public SessionManager() {
    }
}
