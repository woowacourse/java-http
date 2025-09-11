package org.apache.coyote.session;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public Session findSession(final String id) throws IOException {
        if (id == null) {
            return null;
        }
        return SESSIONS.getOrDefault(id, null);
    }

    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }

    private static final SessionManager INSTANCE = new SessionManager();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }
}
