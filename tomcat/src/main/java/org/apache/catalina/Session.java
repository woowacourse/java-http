package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(String name, Object value) {
        this.id = UUID.randomUUID()
                .toString();
        values.put(name, value);
    }

    public String getId() {
        return id;
    }
}
