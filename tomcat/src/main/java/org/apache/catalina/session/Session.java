package org.apache.catalina.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Session {

    private final Map<String, Object> values = new ConcurrentHashMap<>();

    public Session() {
    }

    public void setAttribute(String key, Object value) {
        values.put(key, value);
    }
}
