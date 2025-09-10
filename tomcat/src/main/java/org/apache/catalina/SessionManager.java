package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.util.Cookie;

public class SessionManager {

    public static final String JSESSIONID = "JSESSIONID";
    private static final Map<String, Session> SESSIONS = new HashMap<>();
    private static final SessionManager INSTANCE = new SessionManager();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public static String generateSessionId() {
        return UUID.randomUUID().toString();
    }

    public static String getSessionId(Cookie cookie) {
        return cookie.get(JSESSIONID).orElse(null);
    }

    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public Session findCustomSession(final String id) {
        return SESSIONS.get(id);
    }

    public void remove(final String id) {
        SESSIONS.remove(id);
    }
}
