package nextstep.jwp.model.httpmessage.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpSession {

    private final String id;
    private final Map<String, Object> sessions = new HashMap<>();

    public HttpSession(String id) {
        this.id = id;
    }

    public HttpSession() {
        this(initSessionId());
    }

    private static String initSessionId() {
        return UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setAttribute(String name, Object value) {
        sessions.put(name, value);
    }

    public Object getAttribute(String name) {
        return sessions.get(name);
    }

    public void removeAttribute(String name) {
        sessions.remove(name);
    }
}
