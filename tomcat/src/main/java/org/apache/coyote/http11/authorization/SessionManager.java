package org.apache.coyote.http11.authorization;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SessionManager {

    public static final SessionManager SESSION_MANAGER = new SessionManager();
    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public Optional<Session> findSession(final String id) {
        return Optional.ofNullable(SESSIONS.get(id));
    }

    public void remove(final String id) {
        SESSIONS.remove(id);
    }

    private SessionManager() {
    }
}
