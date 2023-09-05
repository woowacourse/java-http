package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import nextstep.jwp.model.User;

public class Session {
    private final Map<String, Object> values = new HashMap<>();

    private final String id;

    private Session(final String id) {
        this.id = id;
    }

    public static Session generate() {
        final String id = UUID.randomUUID().toString();
        return new Session(id);
    }

    public void put(final String key, final User value) {
        values.put(key, value);
    }

    public Object get(final String key) {
        return values.get(key);
    }

    public String getId() {
        return id;
    }
}
