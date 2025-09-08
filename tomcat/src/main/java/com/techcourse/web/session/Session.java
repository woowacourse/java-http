package com.techcourse.web.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;

public class Session {

    @Getter
    private final String id = UUID.randomUUID().toString();

    private final Map<String, Object> values = new HashMap<>();

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
        SessionManager.getInstance().remove(this);
    }
}
