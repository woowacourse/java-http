package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public static void addSession(String key, Session session) {
        SESSIONS.put(key, session);
    }

    public static boolean containsSession(String id) {
        return SESSIONS.containsKey(id);
    }
}
