package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.common.Session;

public class SessionManager {

    public static final String SESSION_ID_COOKIE_NAME = "JSESSIONID";
    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    public boolean contains(final String sessionId) {
        return SESSIONS.containsKey(sessionId);
    }
}
