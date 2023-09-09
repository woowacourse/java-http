package org.apache.coyote.http11.auth;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private final Map<String, Session> sessions;

    public SessionManager() {
        this.sessions = new ConcurrentHashMap<>();
    }

    public void add(final Session session) {
        sessions.put(session.getId(), session);
    }

    public Session findSession(final String id) {
        if (Objects.isNull(id)) {
            return null;
        }

        return sessions.getOrDefault(id, null);
    }

    public void remove(final String id) {
        sessions.remove(id);
    }
}
