package org.apache.coyote.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {

    public static final String JSESSIONID = "JSESSIONID";

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(String id) {
        this.id = id;
    }

    public Session() {
        this(UUID.randomUUID().toString());
    }

    public String getId() {
        return id;
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }
}
