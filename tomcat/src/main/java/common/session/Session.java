package common.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;

public class Session {

    @Getter
    private final String id = UUID.randomUUID().toString();

    private final Map<String, Object> values = new ConcurrentHashMap<>();

    public Object getAttribute(final String name) {
        validateAttributeName(name);
        return values.get(name);
    }

    public void setAttribute(final String name, final Object value) {
        validateAttributeName(name);
        if (value == null) {
            removeAttribute(name);
            return;
        }
        values.put(name, value);
    }

    public void removeAttribute(final String name) {
        validateAttributeName(name);
        values.remove(name);
    }

    private void validateAttributeName(final String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("속성 이름은 null이거나 비어있을 수 없습니다");
        }
    }
}
