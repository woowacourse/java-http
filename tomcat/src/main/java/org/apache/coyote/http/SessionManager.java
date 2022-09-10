package org.apache.coyote.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public static Optional<Session> add() {
        final Session session = new Session(UUID.randomUUID().toString());
        SESSIONS.put(session.getId() , session);
        return findSession(session.getId());
    }

    public static Optional<Session> findSession(String id) {
        return Optional.ofNullable(SESSIONS.get(id));
    }

    public static void remove(Session session) {
        SESSIONS.remove(session);
    }

    public SessionManager() {
    }
}
