package nextstep.jwp.http.session;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class HttpSession {

    private final String id;
    private final Map<String, Object> values = new ConcurrentHashMap<>();

    HttpSession(String id) {
        this.id = id;
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }

    public Optional<Object> getAttribute(String name) {
        return Optional.ofNullable(values.get(name));
    }

    public void removeAttribute(String name) {
        values.remove(name);
    }

    public void invalidate() {
        values.clear();
    }

    public String getId() {
        return id;
    }

    public boolean hasAttribute(String name) {
        return values.containsKey(name);
    }
}
