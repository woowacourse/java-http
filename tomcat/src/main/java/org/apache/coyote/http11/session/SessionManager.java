package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private SessionManager() {
    }

    public static void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Optional<Session> findSession(String id) {
        if (SESSIONS.containsKey(id)) {
            return Optional.of(SESSIONS.get(id));
        }
        return Optional.empty();
    }

    public static void remove(String id) {
        SESSIONS.remove(id);
    }
}
