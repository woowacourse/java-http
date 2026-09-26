package org.apache.coyote.http11.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Session {
    private final String id;
    private final Map<String, Object> attributes = new HashMap<>();

    private Session(String id) {
        this.id = id;
    }

    public static Session create(String sessionId) {
        return new Session(sessionId);
    }

    public String getId() {
        return id;
    }

    public void setAttribute(final String name, final Object value) {
        attributes.put(name, value);
    }

    public Optional<Object> getAttribute(final String name) {
        return Optional.ofNullable(attributes.get(name));
    }

    public void removeAttribute(final String name) {
        attributes.remove(name);
    }
}
