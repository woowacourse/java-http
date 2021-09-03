package nextstep.jwp.model.web.sessions;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {
    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public HttpSession(String id) {
        this.id = id;
    }

    public void setAttribute(String key, Object value) {
        values.put(key, value);
    }
}
