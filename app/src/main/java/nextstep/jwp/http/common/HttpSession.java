package nextstep.jwp.http.common;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {

    private final String id;
    private final Map<String, Object> values = new HashMap();

    public HttpSession(String id) {
        this.id = id;
    }

    public void setAttribute(String key, Object value) {
        values.put(key, value);
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(String key) {
        return values.get(key);
    }

    public void removeAttribute(String user) {
        values.remove(user);
    }
}
