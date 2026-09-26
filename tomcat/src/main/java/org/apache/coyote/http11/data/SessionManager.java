package org.apache.coyote.http11.data;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private final static Map<String, Session> sessions = new ConcurrentHashMap<>();
    public final static String JSESSIONID_COOKIE_NAME = "JSESSIONID";

    public static Optional<Session> getSession(String sessionId) {
        return Optional.ofNullable(sessions.get(sessionId));
    }

    public static Session createSession() {
        String sessionId = generateSessionId();
        Session session = Session.create(sessionId);
        sessions.put(sessionId, session);
        return session;
    }

    public static void invalidateSession(String sessionId) {
        sessions.remove(sessionId);
    }

    private static String generateSessionId() {
        return UUID.randomUUID().toString();
    }
}
