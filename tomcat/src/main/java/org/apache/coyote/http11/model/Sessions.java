package org.apache.coyote.http11.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Sessions {

    private static final Map<String, Session> sessions = new HashMap<>();

    public static void addNew(final String sessionId, final Session session) {
        sessions.put(sessionId, session);
    }

    public static Optional<Session> find(final String sessionId) {
        if (sessions.containsKey(sessionId)) {
            return Optional.of(sessions.get(sessionId));
        }
        return Optional.empty();
    }
}
