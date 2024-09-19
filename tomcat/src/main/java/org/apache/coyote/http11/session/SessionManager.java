package org.apache.coyote.http11.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public static void add(String key, Session session) {
        SESSIONS.put(key, session);
    }

    public static boolean containsSession(String jsessionid) {
        return SESSIONS.containsKey(jsessionid);
    }
}
