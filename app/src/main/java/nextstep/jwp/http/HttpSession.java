package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpSession {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public HttpSession(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }

    public boolean hasAttribute(String name) {
        final Object attribute = values.get(name);
        return !Objects.isNull(attribute);
    }

    public Object getAttribute(String name) {
        return values.get(name);
    }

    public void removeAttribute(String name) {
        values.remove(name);
    }

    public void invalidate() {
    }
}
