package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private SessionManager() {
        throw new IllegalStateException("Utility class");
    }

    public static void add(final Session session) {
        final String id = session.getId();
        SESSIONS.put(id, session);
    }

    public static Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    public static boolean existSession(final String id) {
        return SESSIONS.containsKey(id);
    }

    public static void remove(final Session session) {
        final String key = SESSIONS.keySet().stream()
                .filter(id -> SESSIONS.get(id).equals(session))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 세션이 없습니다"));
        SESSIONS.remove(key);
    }

    public static String findIdBySession(final Session session) {
        return SESSIONS.keySet().stream()
                .filter(id -> SESSIONS.get(id).equals(session))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 세션이 없습니다"));
    }
}
