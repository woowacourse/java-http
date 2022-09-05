package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private static final Map<String, Object> sessions = new HashMap<>();

    public static void put(String sessionId, Object object) {
        sessions.put(sessionId, object);
    }

    public static Object getBySessionId(String sessionId) {
        return sessions.get(sessionId);
    }
}
