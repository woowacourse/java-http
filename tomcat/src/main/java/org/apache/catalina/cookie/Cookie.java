package org.apache.catalina.cookie;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class Cookie {
    private static final String SESSION_ID = "JSESSIONID";

    private final Map<String, String> values;

    public Cookie(Map<String, String> values) {
        this.values = new HashMap<>(values);
    }

    public Cookie() {
        this.values = new HashMap<>();
    }

    public static Cookie ofJSessionId(String id) {
        return new Cookie(Map.of(SESSION_ID, id));
    }

    public void add(Cookie cookie) {
        this.values.putAll(cookie.values);
    }

    @Nullable
    public String get(String key) {
        return values.get(key);
    }

    @Nullable
    public String getSessionId() {
        return values.get(SESSION_ID);
    }
}
