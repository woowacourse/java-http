package org.apache.coyote.http11.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Session {
    private final String id;
    private final Map<String, String> values = new HashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public static Session createEmptySession() {
        return new Session(null);
    }

    public void setAttribute(final String name, final String value) {
        values.put(name, value);
    }

    public String getId() {
        return id;
    }

    public boolean isExist() {
        return !Objects.isNull(id);
    }
}
