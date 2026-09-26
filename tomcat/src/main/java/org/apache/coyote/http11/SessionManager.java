package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {
    private static final SessionManager INSTANCE = new SessionManager();
    private final Map<String, Session> sessions = new HashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public Session createSession() {
        String id = UUID.randomUUID().toString();
        Session session = new Session(id);
        sessions.put(id, session);
        return session;
    }

    public Session findSession(String id) {
        if (id == null) {
            return null;
        }
        return sessions.get(id);
    }
}
