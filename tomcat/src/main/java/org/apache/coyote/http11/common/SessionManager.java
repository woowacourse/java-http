package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {

    private static final Map<UUID, Session> SESSIONS = new HashMap<>();

    private SessionManager() {

    }

    public static void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Session findSession(UUID id) {
        return SESSIONS.get(id);
    }

    public static void remove(Session session) {
        SESSIONS.remove(session.getId());
    }
}
