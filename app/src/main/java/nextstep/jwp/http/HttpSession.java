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

    public Object getAttribute(String attribute) {
        checkContainsAttribute(attribute, "검색할 요소가 존재하지 않습니다.");
        return values.get(attribute);
    }

    public void removeAttribute(String attribute) {
        checkContainsAttribute(attribute, "삭제할 요소가 존재하지 않습니다.");
        values.remove(attribute);
    }

    private void checkContainsAttribute(String attribute, String errorMessage) {
        if (!values.containsKey(attribute)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public void invalidate() {
        values.keySet()
                .forEach(this::removeAttribute);
    }
}
