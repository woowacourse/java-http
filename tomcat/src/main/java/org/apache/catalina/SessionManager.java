package org.apache.catalina;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.util.Cookie;

public class SessionManager implements org.apache.coyote.SessionManager {

    public static final String JSESSIONID = "JSESSIONID";
    private final Map<String, org.apache.coyote.Session> SESSIONS = new ConcurrentHashMap<>();

    public SessionManager() {
    }

    @Override
    public org.apache.coyote.Session findSession(final String id) {
        if (id == null) {
            return null;
        }
        return SESSIONS.get(id);
    }

    @Override
    public org.apache.coyote.Session createSession() {
        while (true) {
            String sessionId = UUID.randomUUID().toString();
            org.apache.coyote.Session candidate = new Session(sessionId);
            if (SESSIONS.putIfAbsent(sessionId, candidate) == null) {
                return candidate;
            }
        }
    }

    @Override
    public void add(final org.apache.coyote.Session session) {
        if (session == null || session.getId() == null) {
            throw new IllegalArgumentException("session or id is null");
        }
        if (SESSIONS.putIfAbsent(session.getId(), session) != null) {
            throw new IllegalStateException("Duplicate session id: " + session.getId());
        }
    }

    @Override
    public void remove(final String id) {
        SESSIONS.remove(id);
    }

    @Override
    public String getSessionId(final Cookie cookie) {
        if (cookie == null) {
            return null;
        }
        return cookie.get(JSESSIONID).orElse(null);
    }
}
