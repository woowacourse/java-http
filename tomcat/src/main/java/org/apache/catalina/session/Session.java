package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();
    private boolean valid = true;

    public Session(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setAttribute(String name, Object value) {
        checkValid();
        if (value == null) {
            removeAttribute(name);
            return;
        }
        values.put(name, value);
    }

    public Object getAttribute(String name) {
        checkValid();
        return values.get(name);
    }

    public void removeAttribute(String name) {
        checkValid();
        values.remove(name);
    }

    public void invalidate() {
        checkValid();
        SessionManager.getInstance().remove(id);
        values.clear();
        valid = false;
    }

    private void checkValid() {
        if (!valid) {
            throw new IllegalStateException("무효화된 세션입니다.");
        }
    }

}
