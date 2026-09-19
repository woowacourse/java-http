package org.apache.catalina;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SessionManager {
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static Session create() {
        return create(UUID.randomUUID().toString());
    }

    public static Session create(final String id) {
        final var session = new Session(id);

        SESSIONS.put(session.getId(), session);

        return session;
    }

    public static Optional<Session> findSession(final String id) {
        return Optional.ofNullable(SESSIONS.get(id));
    }

    public static void remove(final String id) {
        SESSIONS.remove(id);
    }
}
