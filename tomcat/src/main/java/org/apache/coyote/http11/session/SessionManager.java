package org.apache.coyote.http11.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static boolean isExist(String id) {
        return SESSIONS.containsKey(id);
    }

    public static void remove(String id) {
        SESSIONS.remove(id);
    }
}
