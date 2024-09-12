package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session() {
        this.id = UUID.randomUUID().toString();
    }

    public void addAttribute(String key, Object value) {
        values.put(key, value);
    }

    public boolean hasId(String id) {
        return this.id.equals(id);
    }

    public String getId() {
        return this.id;
    }
}
