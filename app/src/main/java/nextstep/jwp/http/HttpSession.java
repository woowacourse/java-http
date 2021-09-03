package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {

    private final String id;
    private final Map<String, Object> value = new HashMap<>();

    public HttpSession(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setAttribute(String key, Object value) {
        this.value.put(key, value);
    }

    public Object getAttribute(String key) {
        return this.value.get(key);
    }

    public void removeAttribute(String key) {
        this.value.remove(key);
    }

    public void invalidate() {

    }
}
