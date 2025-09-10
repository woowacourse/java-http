package com.techcourse.db;

import com.techcourse.model.Session;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public static Session createSession() {
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId);
        sessions.put(sessionId, session);
        return session;
    }

    public static Optional<Session> findSession(String id) {
        return Optional.ofNullable(sessions.get(id));
    }

    public static void removeSession(String id) {
        sessions.remove(id);
    }
}
