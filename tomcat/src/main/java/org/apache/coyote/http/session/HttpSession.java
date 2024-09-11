package org.apache.coyote.http.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpSession {

    private final UUID id;
    private final Map<String, Object> attributes = new HashMap<>();

    public HttpSession(final UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public Object getAttribute(final String name) {
        return attributes.get(name);
    }

    public void setAttribute(final String name, final Object value) {
        attributes.put(name, value);
    }

    public void removeAttribute(final String name) {
        attributes.remove(name);
    }

    public void invalidate() {
        attributes.clear();
    }
}
