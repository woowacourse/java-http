package org.apache.coyote.http11.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Session {

    private final Map<String, Object> session;

    public Session(final String key, final Object object) {
        this.session = new HashMap<>();
        this.session.put(key, object);
    }

    public Optional<Object> get(final String key) {
        if (session.containsKey(key)) {
            return Optional.of(session.get(key));
        }
        return Optional.empty();
    }
}
