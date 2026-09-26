package org.apache.catalina;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class SessionManager {
    private static final ConcurrentMap<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static Session create() {
        return create(UUID.randomUUID().toString());
    }

    public static Session create(final String id) {
        return SESSIONS.computeIfAbsent(id, Session::new);
    }

    public static Optional<Session> findSession(final String id) {
        return Optional.ofNullable(SESSIONS.get(id));
    }

    public static void remove(final String id) {
        SESSIONS.remove(id);
    }
}
