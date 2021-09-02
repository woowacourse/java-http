package nextstep.jwp.ui.common;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {
    private final String id;
    private final Map<String, Object> httpSession = new HashMap<>();

    public HttpSession(String id) {
        this.id = id;
    }

    public void setAttribute(String name, Object value) {
        httpSession.put(name, value);
    }

    public Object getAttribute(String name) {
        return httpSession.get(name);
    }

    public String getId() {
        return id;
    }
}
