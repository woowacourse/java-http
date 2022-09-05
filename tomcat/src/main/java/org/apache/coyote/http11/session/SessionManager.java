package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.ToString;

@ToString
public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private SessionManager() {
    }

    public static Session create() {
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId);
        SESSIONS.put(sessionId, session);
        return session;
    }

    public static Optional<Session> findSession(final String sessionId) {
        return Optional.ofNullable(SESSIONS.get(sessionId));
    }

    public void remove(final String sessionId) {
        SESSIONS.remove(sessionId);
    }
}
