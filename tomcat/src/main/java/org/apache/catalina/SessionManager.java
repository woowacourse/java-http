package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private SessionManager() {
    }

    public static Optional<Session> find(String sessionId) {
        return Optional.ofNullable(SESSIONS.get(sessionId));
    }

    public static Session create() {
        Session session = new Session(UUID.randomUUID().toString());
        SESSIONS.put(session.getId(), session);
        return session;
    }
}
