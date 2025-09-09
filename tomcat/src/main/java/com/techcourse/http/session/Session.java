package com.techcourse.http.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {
    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public static Session newSession() {
        return new Session(String.valueOf(UUID.randomUUID()));
    }

    public String getId() {
        return id;
    }

    public void setAttribute(final String name, final Object value) {
        values.put(name, value);
    }
}
