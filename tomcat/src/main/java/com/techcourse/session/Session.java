package com.techcourse.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Session {
    private final String jsessionId;
    private final Map<String, Object> values = new ConcurrentHashMap<>();

    public Session() {
        this.jsessionId = UUID.randomUUID().toString();
    }

    public String getJsessionId() {
        return jsessionId;
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
}
