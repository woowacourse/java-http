package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();
    private boolean active = true;

    public Session(final String id) {
        this.id = id;
    }

    public boolean isInvalidate() {
        return !active;
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(final String name) {
        if (!active) {
            throw new IllegalStateException("무효화 된 세션입니다");
        }
        if (values.containsKey(name)) {
            return values.get(name);
        }
        throw new IllegalArgumentException("속성 이름이 세션에 존재하지 않습니다.");
    }

    public void setAttribute(final String name, final Object value) {
        if (!active) {
            throw new IllegalStateException("무효화 된 세션입니다");
        }
        values.put(name, value);
    }

    public void removeAttribute(final String name) {
        if (!active) {
            throw new IllegalStateException("무효화 된 세션입니다");
        }
        values.remove(name);
    }

    public void invalidate() {
        values.clear();
        active = false;
    }
}
