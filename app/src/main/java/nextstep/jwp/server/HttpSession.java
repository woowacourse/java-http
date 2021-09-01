package nextstep.jwp.server;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.SessionAttributeNotFoundException;

public class HttpSession {

    private final String id;
    private final Map<String, Object> values;

    public HttpSession(String id) {
        this.id = id;
        this.values = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }

    public Object getAttribute(String name) {
        if (values.containsKey(name)) {
            return values.get(name);
        }

        throw new SessionAttributeNotFoundException();
    }
}
