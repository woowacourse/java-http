package nextstep.jwp.framework.infrastructure.http.session;

import java.util.Map;

public class HttpSession {

    private final String id;
    private final Map<String, Object> storage;

    public HttpSession(String id, Map<String, Object> storage) {
        this.id = id;
        this.storage = storage;
    }

    public void setAttribute(String key, Object value) {
        storage.put(key, value);
    }

    public Object getAttribute(String key) {
        return storage.get(key);
    }
}
