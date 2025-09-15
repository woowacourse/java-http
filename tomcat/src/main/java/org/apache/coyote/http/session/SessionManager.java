package org.apache.coyote.http.session;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public static void save(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Optional<Session> findById(final String id) {
        if (SESSIONS.containsKey(id)) {
            return Optional.ofNullable(SESSIONS.get(id));
        }
        return Optional.empty();
    }
}
