package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {

    private final String id;
    private final Map<String, Object> items = new HashMap<>();

    public HttpSession(final String id) {
        this.id = id;
    }

    public Object getAttribute(final String name) {
        return items.get(name);
    }

    public void setAttribute(final String name, final Object value) {
        items.put(name, value);
    }

    public void removeAttribute(final String name) {
        items.remove(name);
    }

    public String getId() {
        return id;
    }
}
