package org.apache.coyote.http.session;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public static void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Session find(final String sessionId) {
        return SESSIONS.get(sessionId);
    }

}
