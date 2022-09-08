package org.apache.coyote.http11.model.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager  {

    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public static void add(final String key, final Session session) {
        sessions.put(key, session);
    }

    public static boolean findSession(final String key) {
        return sessions.containsKey(key);
    }

    public static Cookie createCookie() {
        UUID uuid = UUID.randomUUID();
        return new Cookie("JSESSIONID", uuid.toString());
    }

    public static void remove(final String key) {
        sessions.remove(key);
    }
}
