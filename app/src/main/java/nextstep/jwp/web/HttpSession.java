package nextstep.jwp.web;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {
    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    HttpSession(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setAttribute(String name, Object value) {
        this.values.put(name, value);
    }

    public Object getAttribute(String name) {
        return this.values.get(name);
    }

    public void removeAttribute(String name) {
        this.values.remove(name);
    }

    public void invalidate() {
        this.values.keySet().forEach(this.values::remove);
    }
}
