package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.util.Cookie;

public class SessionManager implements org.apache.coyote.SessionManager {

    public static final String JSESSIONID = "JSESSIONID";
    private final Map<String, org.apache.coyote.Session> SESSIONS = new HashMap<>();

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
        String sessionId = UUID.randomUUID().toString();
        org.apache.coyote.Session session = new Session(sessionId);
        add(session);
        return session;
    }

    @Override
    public void add(final org.apache.coyote.Session session) {
        SESSIONS.put(session.getId(), session);
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
