package org.apache.coyote.http11.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SessionManager {

    private final Map<String, Session> sessions;

    public SessionManager() {
        this.sessions = new HashMap<>();
    }

    public void add(final Session session) {
        sessions.put(session.getId(), session);
    }

    public Session findSession(final String id) {
        return sessions.get(id);
    }

    public void remove(final String id) {
        sessions.remove(id);
    }
}
