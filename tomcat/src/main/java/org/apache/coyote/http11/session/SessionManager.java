package org.apache.coyote.http11.session;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();
    private static final int MAX_INACTIVE_INTERVAL = 60 * 1000 * 15; // 15분

    private final int maxInactiveInterval;

    public SessionManager() {
        this(MAX_INACTIVE_INTERVAL);
    }

    public SessionManager(final int maxInactiveInterval) {
        this.maxInactiveInterval = maxInactiveInterval;
    }

    public Session createSession(final LocalDateTime time) {
        final Session session = new Session(time, maxInactiveInterval);
        SESSIONS.put(session.getId(), session);
        return session;
    }

    public Optional<Session> findSession(final String id) {
        return Optional.ofNullable(SESSIONS.get(id));
    }

    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }

    public void cleanUpSession(final LocalDateTime time) {
        SESSIONS.entrySet()
                .stream()
                .filter(entry -> entry.getValue()
                        .isExpired(time))
                .forEach(entry -> SESSIONS.remove(entry.getKey()));
    }
}
