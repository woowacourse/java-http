package org.apache.coyote.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class HttpSession {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public HttpSession() {
        this(UUID.randomUUID().toString());
    }

    public HttpSession(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(final String name) {
        return values.get(name);
    }

    public void setAttribute(final String name, final Object value) {
        values.put(name, value);
    }

    public void removeAttribute(final String name) {
        values.remove(name);
    }

    public void invalidate() {
        values.clear();
    }

    @Override
    public boolean equals(final Object target) {
        if (this == target) {
            return true;
        }
        if (target == null || getClass() != target.getClass()) {
            return false;
        }
        final HttpSession targetHttpSession = (HttpSession) target;
        return Objects.equals(getId(), targetHttpSession.getId()) && Objects.equals(values, targetHttpSession.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), values);
    }
}
