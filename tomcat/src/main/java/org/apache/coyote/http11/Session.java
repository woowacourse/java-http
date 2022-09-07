package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public void addAttribute(final String key, final Object value) {
        values.put(key, value);
    }

    public Object getAttribute(final String key) {
        return values.get(key);
    }

    public String getId() {
        return id;
    }
}
