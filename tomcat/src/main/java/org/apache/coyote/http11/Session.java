package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class Session  {
    private final String id;
    private final Map<String, Object> params = new HashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(final String name) {
        return params.get(name);
    }

    public void setAttribute(final String name, final Object value) {
        params.put(name, value);
    }
}
