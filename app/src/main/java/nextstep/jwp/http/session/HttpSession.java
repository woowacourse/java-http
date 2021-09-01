package nextstep.jwp.http.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpSession {

    private final String id;
    private final Map<String, Object> sessionTable = new HashMap<>();

    public HttpSession() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setAttribute(String name, Object value) {
        sessionTable.put(name, value);
    }

    public Object getAttribute(String name) {
        return sessionTable.get(name);
    }

    public void removeAttribute(String name) {
        sessionTable.remove(name);
    }

    public void invalidate() {
        sessionTable.clear();
    }
}
