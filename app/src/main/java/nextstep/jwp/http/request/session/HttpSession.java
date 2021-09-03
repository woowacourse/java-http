package nextstep.jwp.http.request.session;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {

    private final String id;
    private Map<String, Object> values = new HashMap<>();

    HttpSession(String id) {
        this.id = id;
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

    public void removeAttribute(String name) {
        values.remove(name);
    }
}
