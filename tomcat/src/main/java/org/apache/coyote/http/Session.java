package org.apache.coyote.http;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public void add(final String key, final String data) {
        values.put(key, data);
    }

    public String getId() {
        return id;
    }
}
