package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {

    private final String sessionId;
    private final Map<String, Object> attributes;

    private Session(
            final String sessionId,
            final Map<String, Object> attributes
    ) {
        this.sessionId = sessionId;
        this.attributes = attributes;
    }

    public static Session create() {
        final String id = UUID.randomUUID().toString();
        final Map<String, Object> attributes = new HashMap<>();
        return new Session(id, attributes);
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public Object getAttribute(final String name) {
        return this.attributes.get(name);
    }

    public void setAttribute(
            final String name,
            final Object value
    ) {
        this.attributes.put(name, value);
    }
}
