package nextstep.jwp.http.message.element.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class HttpSession implements Session {

    private final String sessionId;
    private final Map<String, Object> values;

    public HttpSession() {
        this(UUID.randomUUID().toString(), new HashMap<>());
    }

    public HttpSession(String sessionId) {
        this(sessionId, new HashMap<>());
    }

    public HttpSession(String sessionId, Map<String, Object> values) {
        this.sessionId = sessionId;
        this.values = values;
    }

    @Override
    public Optional<Object> getAttribute(String key) {
        return Optional.ofNullable(values.get(key));
    }

    @Override
    public void setAttribute(String key, Object value) {
        values.put(key, value);
    }

    @Override
    public void removeAttribute(String key) {
        values.remove(key);
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }
}
