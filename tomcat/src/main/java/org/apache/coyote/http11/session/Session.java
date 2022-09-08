package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.ToString;

@ToString
public class Session {

    @Getter
    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public Optional<Object> getAttribute(final String key) {
        return Optional.ofNullable(values.get(key));
    }

    public void setAttribute(final String key, final Object value) {
        values.put(key, value);
    }

    public void removeAttribute(final String key) {
        values.remove(key);
    }

    public void invalidate() {
        values.clear();
    }
}
