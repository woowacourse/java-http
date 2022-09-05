package org.apache.coyote.servlet.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.servlet.cookie.HttpCookie;

public class SessionRepository {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public Session findSession(String sessionId) {
        return SESSIONS.get(sessionId);
    }

    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public boolean isValidSessionCookie(HttpCookie sessionCookie) {
        if (sessionCookie == null) {
            return false;
        }
        final var sessionId = sessionCookie.getValue();
        final var savedSession = SESSIONS.get(sessionId);
        return savedSession != null;
    }
}
