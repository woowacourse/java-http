package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final SessionManager INSTANCE = new SessionManager();
    private final Map<String, Session> SESSIONS = new HashMap<>();

    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }

    private SessionManager() {
    }
}
