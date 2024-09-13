package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Session {

    private final String id;
    private final Map<String, Object> values;

    public Session(String id) {
        this.id = id;
        this.values = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setAttribute(String key, Object object) {
        values.put(key, object);
    }

    public Optional<Object> findAttribute(String key) {
        if (values.containsKey(key)) {
            return Optional.of(values.get(key));
        }
        return Optional.empty();
    }
}
