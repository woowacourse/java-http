package nextstep.jwp.http.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpSession {

    public static final String SESSION_TYPE = "JSESSIONID";

    private final String id;
    private final Map<String, Object> sessionTable;

    private HttpSession(String id, Map<String, Object> sessionTable) {
        this.id = id;
        this.sessionTable = sessionTable;
    }

    public static HttpSession create() {
        return new HttpSession(UUID.randomUUID().toString(), new HashMap<>());
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

    public boolean contains(String user) {
        return sessionTable.containsKey(user);
    }

    public String getType() {
        return SESSION_TYPE;
    }
}
