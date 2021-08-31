package nextstep.jwp.handler;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    HttpSession(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public boolean containsAttribute(String name) {
        return values.containsKey(name);
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }

    public Object getAttribute(String name) {
        return values.get(name);
    }

    public void removeAttribute(String name) {
        values.remove(name);
    }

    public void invalidate() {
        HttpSessions.remove(id);
    }
}
