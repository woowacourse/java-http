package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public SessionManager() {
    }

    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public boolean contains(final String id) {
        return SESSIONS.containsKey(id);
    }

    public Session findSession(final String id) {
        return SESSIONS.get(id);
    }
}
