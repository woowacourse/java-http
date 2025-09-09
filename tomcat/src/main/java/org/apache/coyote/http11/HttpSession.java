package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpSession {

    public static final String SESSION_TYPE = "JSESSIONID";

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public HttpSession() {
        this.id = String.valueOf(UUID.randomUUID());
    }

    public void setAttribute(
            final String key,
            final Object value
    ) {
        values.put(key, value);
    }

    public Object getValue(final String key) {
        return values.get(key);
    }

    public String getId() {
        return id;
    }
}
