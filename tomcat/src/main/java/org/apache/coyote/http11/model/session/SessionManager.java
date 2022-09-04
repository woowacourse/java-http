package org.apache.coyote.http11.model.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SessionManager  {

    private static final Map<String, Session> sessions = new HashMap<>();

    public static void add(final String key, final Session session) {
        sessions.put(key, session);
    }

    public static Optional<Session> findSession(final String key) {
        if (sessions.containsKey(key)) {
            return Optional.of(sessions.get(key));
        }
        return Optional.empty();
    }

    public static void remove(final String key) {
        sessions.remove(key);
    }
}
