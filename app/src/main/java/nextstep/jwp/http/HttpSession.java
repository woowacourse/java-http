package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpSession {

    private final Map<String, Object> values = new HashMap<>();
    private final String id;

    public HttpSession(final String id) {
        this.id = id;
    }

    public void setAttribute(final String name, final Object value) {
        values.put(name, value);
    }

    public Object getAttribute(final String name){
        return values.get(name);
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final HttpSession that = (HttpSession) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
