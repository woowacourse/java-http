package org.apache.coyote.http11;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Session {

    private final Map<String, Object> sessionStore = new ConcurrentHashMap<>();

    public void addStore(final String key, final Object value) {
        sessionStore.put(key, value);
    }

    public Object getStore(final String key) {
        return sessionStore.get(key);
    }

    public boolean containsStore(final String key) {
        return sessionStore.containsKey(key);
    }
}
