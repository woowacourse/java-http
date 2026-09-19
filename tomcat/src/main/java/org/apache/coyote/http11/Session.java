package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class Session {
    private final String id;
    private final Map<String, Object> values = new HashMap<String, Object>();
    private boolean active = true;

    public Session(String id) {
        this.id = id;
    }

    public String getId() {
        checkValid();
        return id;
    }

    public Object getAttribute(final String name) {
        checkValid();
        return values.get(name);
    }

    public void setAttribute(final String name, final Object value) {
        checkValid();
        values.put(name, value);
    }

    public void removeAttribute(final String name) {
        checkValid();
        values.remove(name);
    }

    public void invalidate() {
        checkValid();
        values.clear();
        SessionManager.remove(id);
        active = false;
    }

    private void checkValid() {
        if (!active) {
            throw new IllegalStateException("무효화된 세션은 사용할 수 없습니다.");
        }
    }
}
