package nextstep.jwp.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
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
}
