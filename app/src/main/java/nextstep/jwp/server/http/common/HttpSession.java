package nextstep.jwp.server.http.common;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {
    private final String id;
    private final Map<String, Object> session;

    public HttpSession(String id) {
        this.id = id;
        this.session = new HashMap<>();
    }

    public void setAttribute(String name, Object value) {
        session.put(name, value);
    }

    public Object getAttribute(String name) {
        return session.get(name);
    }

    public String getId() {
        return id;
    }
}
