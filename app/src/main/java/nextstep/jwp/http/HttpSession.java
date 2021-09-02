package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {
    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public HttpSession(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }

    public Object getAttribute(String name) {
        try {
            return values.get(name);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("검색할 요소가 존재하지 않습니다.");
        }
    }

    public void removeAttribute(String name) {
        try {
            values.remove(name);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("검색할 요소가 존재하지 않습니다.");
        }
    }

    public void invalidate() {
        values.keySet()
                .forEach(this::removeAttribute);
    }
}
