package com.techcourse.db;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemorySessionRepository {

    private static final Map<String, String> sessions = new ConcurrentHashMap<>();

    private InMemorySessionRepository() {
    }

    public static void save(String sessionId, String userAccount) {
        sessions.put(sessionId, userAccount);
    }

    public static Optional<String> findBySessionId(String sessionId) {
        return Optional.ofNullable(sessions.get(sessionId));
    }
}
