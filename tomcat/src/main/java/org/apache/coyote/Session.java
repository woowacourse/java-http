package org.apache.coyote;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Session {

    private final static Map<String, Map<String, Object>> SESSIONS = new HashMap<>();

    private Session() {
    }

    public static void add(final String jsessionid, final String key, final Object value) {
        if(SESSIONS.get(jsessionid) == null) {
            SESSIONS.put(jsessionid, Map.of(key, value));
            return;
        }
        SESSIONS.get(jsessionid).put(key, value);
    }

    public static Optional<Object> find(final String jsessionid, final String key) {
        if (SESSIONS.containsKey(jsessionid)) {
            return Optional.ofNullable(SESSIONS.get(jsessionid).get(key));
        }
        return Optional.empty();
    }
}
