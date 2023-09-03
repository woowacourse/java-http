package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String id;
    private final Map<String, Object> items = new HashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public void setAttribute(final String key, final Object value) {
        items.put(key, value);
    }

    public String getId() {
        return id;
    }

}
