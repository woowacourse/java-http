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

    public void removeAttribute(String attribute) {
        if (!containsAttribute(attribute)) {
            throw new IllegalArgumentException("삭제할 요소가 존재하지 않습니다.");
        }
        values.remove(attribute);
    }

    public boolean containsAttribute(String attribute) {
        return values.containsKey(attribute);
    }
}
