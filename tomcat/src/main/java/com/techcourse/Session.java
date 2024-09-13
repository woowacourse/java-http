package com.techcourse;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String sessionId;
    private final Map<String, Object> attributes = new HashMap<>();

    public Session(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setAttributes(String name, Object value) {
        attributes.put(name, value);
    }
}
