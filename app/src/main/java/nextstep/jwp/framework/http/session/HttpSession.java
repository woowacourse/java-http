package nextstep.jwp.framework.http.session;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    HttpSession(final String id) {
        this.id = id;
    }

    public void removeAttribute(String name) {
        values.remove(name);
    }

    public void invalidate() {
        return;
    }

    public String getId() {
        return id;
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }

    public Object getAttribute(String name) {
        return values.get(name);
    }
}
