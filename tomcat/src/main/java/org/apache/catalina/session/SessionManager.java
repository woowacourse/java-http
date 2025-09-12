package org.apache.catalina.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();
    private static final SessionManager INSTANCE = new SessionManager();

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public Session createSession() {
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId);

        SESSIONS.put(session.getId(), session);
        return session;
    }

    public Session findSession(final String id) {
        return SESSIONS.get(id);
    }
}
