package org.apache.catalina.session;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SessionManager {
    private static final SessionManager sessionManager = new SessionManager(new ConcurrentHashMap<>());

    private ConcurrentMap<String, Session> sessions;

    private SessionManager(ConcurrentMap<String, Session> sessions) {
        this.sessions = sessions;
    }

    public static SessionManager getInstance() {
        return sessionManager;
    }

    public Session findSessionById(String sessionId) {
        return sessions.getOrDefault(sessionId, new Session());
    }

    public String storeSession(Session session) {
        String sessionId = UUID.randomUUID().toString();
        sessions.putIfAbsent(sessionId, session);
        return sessionId;
    }
}
