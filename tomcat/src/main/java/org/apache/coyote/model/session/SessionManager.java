package org.apache.coyote.model.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SessionManager {

    private static final Map<String, Session> sessions = new HashMap<>();

    public static Optional<Session> findSession(final String key) {
        return Optional.ofNullable(sessions.getOrDefault(key, null));
    }

    public static void add(final String key, final Session session) {
        sessions.put(key, session);
    }
}
