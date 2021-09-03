package nextstep.jwp.web.network;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class HttpSession {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public HttpSession(UUID id) {
        this(id.toString());
    }

    public HttpSession(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(String name) {
        return values.get(name);
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }

    public void removeAttribute(String name) {
        values.remove(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpSession session = (HttpSession) o;
        return Objects.equals(id, session.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
