package org.apache.coyote.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    public static boolean hasValidSessionId(Cookie cookie) {
        return cookie.has(JSESSIONID);
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
}
