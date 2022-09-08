package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final Map<String, Object> values = new HashMap<>();

    public Session() {
    }

    public void setAttribute(String key, Object value) {
        values.put(key, value);
    }
}
