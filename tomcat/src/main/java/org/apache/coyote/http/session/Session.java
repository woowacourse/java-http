package org.apache.coyote.http.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Session {

    public static final String COOKIE_KEY = "JSESSIONID";

    private final UUID id;
    private final Map<String, Object> values = new HashMap<>();

    public Session() {
        this.id = UUID.randomUUID();
    }

    public void addValue(String key, Object value) {
        values.put(key, value);
    }

    public Optional<Object> getValue(String key) {
        return Optional.ofNullable(values.get(key));
    }

    public String getId() {
        return id.toString();
    }
}
