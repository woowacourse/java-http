package org.apache.coyote.http11.response;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Session  {
    private final String id;
    private final Map<String, Object> params = new ConcurrentHashMap<>();

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
        params.putIfAbsent(name, value);
    }
}
