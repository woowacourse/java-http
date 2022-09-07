package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(String id) {
        this.id = id;
    }

    public void setAttribute(String key, Object value) {
        values.put(key, value);
    }

    public Object getAttribute(String key) {
        return values.get(key);
    }

    public String getId() {
        return id;
    }
}
