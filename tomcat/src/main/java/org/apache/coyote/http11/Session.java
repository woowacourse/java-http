package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class Session {
    private final String id;
    private final Map<String, String> values = new HashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public String getAttribute(final String name) {
        return values.get(name);
    }

    public void setAttribute(final String name, final String value) {
        values.put(name, value);
    }

    public void removeAttribute(final String name) {
        values.remove(name);
    }
}
