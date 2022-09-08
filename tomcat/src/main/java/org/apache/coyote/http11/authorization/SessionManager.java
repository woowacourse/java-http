package org.apache.coyote.http11.authorization;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    public static final SessionManager SESSION_MANAGER = new SessionManager();

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public Optional<Session> findSession(final String id) {
        return Optional.ofNullable(SESSIONS.get(id));
    }

    public void remove(final String id) {
        SESSIONS.remove(id);
    }
}
