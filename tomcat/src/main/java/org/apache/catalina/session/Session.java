package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final Map<String, Object> values = new HashMap<>();

    public Session() {
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }
}
