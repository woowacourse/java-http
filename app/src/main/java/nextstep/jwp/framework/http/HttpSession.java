package nextstep.jwp.framework.http;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import nextstep.jwp.framework.util.StringUtils;

public class HttpSession {

    public static final String JSESSIONID = "JSESSIONID";

    private final String id;
    private final Map<String, Object> values;

    public HttpSession() {
        this(UUID.randomUUID().toString(), new HashMap<>());
    }

    public HttpSession(String id, Map<String, Object> values) {
        this.id = StringUtils.requireNonBlank(id);
        this.values = new HashMap<>(values);
    }

    public String getId() {
        return id;
    }

    public void setAttribute(String name, Object value) {
        HttpSessions.putIfAbsent(id, this);
        values.put(name, value);
    }

    public Object getAttribute(String name) {
        return values.get(name);
    }
}
