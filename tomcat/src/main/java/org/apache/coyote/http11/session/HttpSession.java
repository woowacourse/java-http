package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {

    private static final Map<String, Object> sessions = new HashMap<>();

    public static void put(String key, Object object) {
        sessions.put(key, object);
    }

    public static Object get(String key) {
        return sessions.get(key);
    }
}
