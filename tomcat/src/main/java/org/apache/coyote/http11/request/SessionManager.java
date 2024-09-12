package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public static void add(String key, Session session) {
        SESSIONS.put(key, session);
    }

    public static boolean containsSession(String jsessionid) {
        return SESSIONS.containsKey(jsessionid);
    }
}
