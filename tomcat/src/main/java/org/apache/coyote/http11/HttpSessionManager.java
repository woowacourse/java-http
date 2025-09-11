package org.apache.coyote.http11;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HttpSessionManager {

    private static final Map<String, HttpSession> SESSIONS = new ConcurrentHashMap<>();

    private HttpSessionManager() {
    }

    public static HttpSession createSession() {
        String id = UUID.randomUUID().toString();
        HttpSession httpSession = new HttpSession(id);
        SESSIONS.put(id, httpSession);
        return httpSession;
    }

    public static HttpSession findSession(final String id) {
        return SESSIONS.get(id);
    }

    public static void remove(final String id) {
        SESSIONS.remove(id);
    }
}
