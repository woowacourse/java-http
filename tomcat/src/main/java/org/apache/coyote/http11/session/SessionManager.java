package org.apache.coyote.http11.session;

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
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(SESSIONS.getOrDefault(id, null));
    }
}
