package nextstep.jwp.http.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpSession {

    private static final HttpSession EMPTY = new HttpSession("");
    private final Map<String, Object> values = new HashMap<>();
    private String id;

    HttpSession(String id) {
        this.id = id;
    }

    public static HttpSession empty() {
        return EMPTY;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }

    public Object getAttribute(String name) {
        return values.get(name);
    }

    public void removeAttribute(String name) {
        values.remove(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpSession that = (HttpSession) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
