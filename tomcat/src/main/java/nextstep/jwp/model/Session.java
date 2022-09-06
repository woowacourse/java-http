package nextstep.jwp.model;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setAttribute(final String name, final Object object) {
        values.put(name, object);
    }
}
