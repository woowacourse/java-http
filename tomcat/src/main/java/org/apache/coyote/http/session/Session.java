package org.apache.coyote.http.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Session {

    public static final String REQUEST_COOKIE_KEY = "JSESSIONID";

    private final UUID jsessionId;
    private final Map<String, Object> session = new HashMap<>();

    public Session() {
        this.jsessionId = UUID.randomUUID();
    }

    public void addValue(String key, Object value) {
        session.put(key, value);
    }

    public Optional<Object> getValue(String key) {
        return Optional.ofNullable(session.get(key));
    }

    public String getJsessionId() {
        return jsessionId.toString();
    }

    @Override
    public String toString() {
        return "Session{" +
            "jsessionId=" + jsessionId +
            ", session=" + session +
            '}';
    }
}
