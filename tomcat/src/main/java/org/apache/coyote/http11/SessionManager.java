package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public Optional<Session> findSession(String id) {
        return Optional.ofNullable(SESSIONS.get(id));
    }

    public Session getSession(String id) {
        return findSession(id).orElseThrow(() -> new IllegalStateException("session이 존재하지 않습니다."));
    }

    public Session generateSession() {
        final UUID sessionId = UUID.randomUUID();
        final Session session = new Session(sessionId.toString());
        SESSIONS.put(session.getId(), session);
        return session;
    }

    public void remove(Session session) {
        SESSIONS.remove(session.getId());
    }
}
