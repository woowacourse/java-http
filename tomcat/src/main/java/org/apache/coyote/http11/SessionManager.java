package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public static Optional<Session> getSession(final String sessionId) {
        return Optional.ofNullable(SESSIONS.get(sessionId));
    }

    public static Session createSession() {
        final Session session = new Session(UUID.randomUUID().toString());
        SESSIONS.put(session.getId(), session);
        return session;
    }
}
