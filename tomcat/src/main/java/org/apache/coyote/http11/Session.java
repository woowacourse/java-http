package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String sessionId;
    private final Map<String, Object> attributes = new HashMap<>();

    public Session(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public void setAttributes(String name, Object value) {
        attributes.put(name, value);
    }
}
