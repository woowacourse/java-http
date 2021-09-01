package nextstep.jwp.domain;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {

    private String id;
    private final Map<String, Object> values = new HashMap<>();

    public HttpSession(String id) {
        this.id = id;
    }

    public void removeAttribute(String name) {
        values.remove(name);
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }

    public Object getAttribute(String name) {
        return values.get(name);
    }

    public String getId() {
        return id;
    }
}
