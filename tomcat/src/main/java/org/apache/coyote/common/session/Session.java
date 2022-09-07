package org.apache.coyote.common.session;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public Object getAttribute(final String name) {
        return values.get(name);
    }

    public void setAttribute(final String name, final Object value) {
        values.put(name, value);
    }

    public void removeAttribute(final String name) {
        values.remove(name);
    }

    public void invalidate() {
        final Set<String> keySet = new HashSet<>(values.keySet());

        for (String key : keySet) {
            removeAttribute(key);
        }
    }
}
