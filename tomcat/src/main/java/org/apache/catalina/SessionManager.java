package org.apache.catalina;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SessionManager {

    private static final SessionManager INSTANCE = new SessionManager();
    private static final ConcurrentMap<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public void setSession(String key, Session session) {
        SESSIONS.put(key, session);
    }

    public Session getSession(String key) {
        return SESSIONS.getOrDefault(key, null);
    }

    public Session createSession() {
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId);
        setSession(sessionId, session);
        return session;
    }
}
