package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String id;

    private final Map<String, Object> values = new HashMap<>();

    public Session(String id) {
        this.id = id;
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }

    public String getId() {
        return this.id;
    }
}
