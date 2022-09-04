package org.apache.coyote.servlet.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.model.User;

public class SessionRepository {

    private static final Map<UUID, Session> SESSIONS = new ConcurrentHashMap<>();

    public UUID generateNewSession(User user) {
        final var sessionId = UUID.randomUUID();
        SESSIONS.put(sessionId, Session.of(user));
        return sessionId;
    }

    public boolean isValidSession(String sessionId) {
        final var session = SESSIONS.get(UUID.fromString(sessionId));
        if (session == null) {
            return false;
        }
        return !session.isExpired();
    }
}
