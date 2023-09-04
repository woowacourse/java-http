package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    private SessionManager() {
    }

    public static SessionManager create() {
        return new SessionManager();
    }

    public void add(final HttpSession httpSession) {
        SESSIONS.put(httpSession.getId(), httpSession);
    }

    public HttpSession findSession(final String id) {
        return SESSIONS.get(String.valueOf(id));
    }
}
