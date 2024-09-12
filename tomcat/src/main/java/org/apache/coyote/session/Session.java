package org.apache.coyote.session;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String id;
    private final Map<String, Object> values;

    public Session(String id) {
        this.id = id;
        this.values = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setAttribute(String key, Object object) {
        values.put(key, object);
    }

    public Object findAttribute(String key) {
        return values.get(key);
    }
}
