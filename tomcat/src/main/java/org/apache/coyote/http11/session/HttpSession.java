package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HttpSession {

    private final String id;
    private final Map<String, Object> sessions = new ConcurrentHashMap<>();

    public HttpSession() {
        this.id = UUID.randomUUID().toString();
    }

    public void put(String key, Object object) {
        sessions.put(key, object);
    }

    public Object get(String key) {
        return sessions.get(key);
    }

    public String getId() {
        return id;
    }
}
