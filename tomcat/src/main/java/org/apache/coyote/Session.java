package org.apache.coyote;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Session {

    private final static Map<String, Map<String, Object>> SESSIONS = new HashMap<>();

    private Session() {
    }

    public static void add(final String jsessionid, final String key, final Object value) {
        if(SESSIONS.get(jsessionid) != null) {
            SESSIONS.get(jsessionid).put(key, value);
        }
        SESSIONS.computeIfAbsent(jsessionid, k -> Map.of(key, value));
    }

    public static Optional<Object> find(final String jsessionid, final String key) {
        if (SESSIONS.containsKey(jsessionid)) {
            return Optional.of(SESSIONS.get(jsessionid).get(key));
        }
        return Optional.empty();
    }
}
