package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {

    private final String id;
    private final Map<String, String> values;

    public Session() {
        this.id = UUID.randomUUID().toString();
        this.values = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public String getAttribute(String key) {
        return values.get(key);
    }

    public void setAttribute(String key, String value) {
        values.put(key, value);
    }
}
