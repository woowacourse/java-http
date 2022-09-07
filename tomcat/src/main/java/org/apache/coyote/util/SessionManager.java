package org.apache.coyote.util;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.http.Session;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private SessionManager() {
    }

    public static void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    public static void remove(final String id) {
        SESSIONS.remove(id);
    }
}