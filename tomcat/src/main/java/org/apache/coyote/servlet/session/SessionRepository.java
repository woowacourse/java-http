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
}
