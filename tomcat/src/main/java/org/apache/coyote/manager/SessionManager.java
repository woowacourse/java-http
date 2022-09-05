package org.apache.coyote.manager;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public static void add(final String id, final Session session) {
        SESSIONS.put(id, session);
    }

    public static boolean hasSession(final String id) {
        return SESSIONS.containsKey(id);
    }
}
