package nextstep.jwp.http.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public Optional<Object> getAttribute(final String key) {
        if (values.containsKey(key)) {
            return Optional.ofNullable(values.get(key));
        }
        return Optional.empty();
    }


    public void addAttribute(final String key, final Object value) {
        values.put(key, value);
    }

    public void removeAttribute(final String key) {
        values.remove(key);
    }

    public String getId() {
        return id;
    }
}
