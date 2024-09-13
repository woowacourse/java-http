package com.techcourse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Session {

    private final String sessionId;
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();

    public Session(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setAttributes(String name, Object value) {
        attributes.put(name, value);
    }
}
