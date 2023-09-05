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
        if (!SESSIONS.containsKey(id)) {
            throw new IllegalArgumentException("존재하지 않는 세션입니다.");
        }
        return SESSIONS.get(id);
    }

    public static void remove(Session session) {
        SESSIONS.remove(session.getId());
    }
}
