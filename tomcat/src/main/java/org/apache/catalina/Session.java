package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {

    public static final String SESSION_KEY = "JSESSIONID";

    private final String id = UUID.randomUUID().toString();
    private final Map<String, Object> values = new HashMap<>();

    public String getId() {
        return id;
    }

    public Object getAttribute(final String name) {
        return values.get(name);
    }

    public void setAttribute(final String name, final Object value) {
        values.put(name, value);
    }
}
