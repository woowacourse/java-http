package org.apache.catalina;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements CustomManager {

    private final static Map<String, Map<String, Object>> SESSIONS = new ConcurrentHashMap<>();
    private final static SessionManager INSTANCE = new SessionManager();
    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(final String jsessionid, final String key, final Object value) {
        if(SESSIONS.get(jsessionid) == null) {
            SESSIONS.put(jsessionid, Map.of(key, value));
            return;
        }
        SESSIONS.get(jsessionid).put(key, value);
    }

    @Override
    public Optional<Object> find(final String jsessionid, final String key) {
        if (SESSIONS.containsKey(jsessionid)) {
            return Optional.ofNullable(SESSIONS.get(jsessionid).get(key));
        }
        return Optional.empty();
    }
}
