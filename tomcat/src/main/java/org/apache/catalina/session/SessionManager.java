package org.apache.catalina.session;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public Optional<Session> find(final String sessionId) {
        if (sessionId == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(sessions.get(sessionId));
    }

    public Session create() {
        String sessionId = UUID.randomUUID().toString();

        while (sessions.containsKey(sessionId)) {
            sessionId = UUID.randomUUID().toString();
        }

        final Session session = Session.from(sessionId);
        sessions.put(sessionId, session);

        return session;
    }
}
