package org.apache.session;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Optional<Session> findSession(final String id) {
        if (SESSIONS.containsKey(id)) {
            return Optional.of(SESSIONS.get(id));
        }
        return Optional.empty();
    }

    public static void remove(final Session session) {
        for (String id : SESSIONS.keySet()) {
            if (SESSIONS.get(id).equals(session)) {
                SESSIONS.remove(id);
            }
        }
    }
}
