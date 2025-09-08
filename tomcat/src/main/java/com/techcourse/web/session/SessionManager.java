package com.techcourse.web.session;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.catalina.Manager;

@NoArgsConstructor(access = AccessLevel.NONE)
public class SessionManager implements Manager {

    private static final SessionManager INSTANCE = new SessionManager();

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(final Session session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public Optional<Session> find(final String id) {
        return Optional.ofNullable(sessions.get(id));
    }

    @Override
    public void remove(final Session session) {
        sessions.remove(session.getId());
    }

    public boolean isValidSession(final String sessionId) {
        return isNotNullOrEmpty(sessionId) && sessionExists(sessionId);
    }

    public void clear() {
        sessions.clear();
    }

    private boolean isNotNullOrEmpty(final String sessionId) {
        return sessionId != null && !sessionId.trim().isEmpty();
    }

    private boolean sessionExists(final String sessionId) {
        return sessions.containsKey(sessionId);
    }
}
