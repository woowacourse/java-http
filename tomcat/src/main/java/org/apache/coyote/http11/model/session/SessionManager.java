package org.apache.coyote.http11.model.session;

import java.util.HashMap;
import java.util.Map;

public class SessionManager  {

    private static final Map<String, Session> sessions = new HashMap<>();

    public static void add(final String key, final Session session) {
        sessions.put(key, session);
    }

    public static boolean findSession(final String key) {
        return sessions.containsKey(key);
    }

    public static void remove(final String key) {
        sessions.remove(key);
    }
}
