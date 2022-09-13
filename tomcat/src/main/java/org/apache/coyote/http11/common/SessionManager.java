package org.apache.coyote.http11.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final ConcurrentHashMap<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static boolean hasSession(final String id) {
        return SESSIONS.containsKey(id);
    }

    public static Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    public static void remove(final String id) {
        SESSIONS.remove(id);
    }

}
