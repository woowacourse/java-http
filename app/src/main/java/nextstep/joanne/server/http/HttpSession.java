package nextstep.joanne.server.http;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {

    private final String id;
    private Map<String, Object> values = new HashMap<>();

    private HttpSession(String id) {
        this.id = id;
    }

    public static HttpSession of(String id) {
       final HttpSession httpSession = new HttpSession(id);
       HttpSessions.addSession(id, httpSession);
       return httpSession;
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
        HttpSessions.update(id, this);
    }

    public Object getAttribute(String name) {
        return values.get(name);
    }

    public void removeAttribute(String name) {
        values.remove(name);
    }

    public void expire() {
        HttpSessions.remove(id);
        this.values = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public Map<String, Object> getValues() {
        return values;
    }
}
