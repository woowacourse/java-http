package org.apache.coyote.servlet.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionRepository {

    private final Map<UUID, Session> value = new ConcurrentHashMap<>();

    public UUID generateNewSession(Long userId) {
        final var sessionId = UUID.randomUUID();
        value.put(sessionId, Session.of(userId));
        return sessionId;
    }

    public boolean isValidSession(String sessionId) {
        final var session = value.get(UUID.fromString(sessionId));
        if (session == null) {
            return false;
        }
        return !session.isExpired();
    }
}
