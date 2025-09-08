package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    private final Map<String, String> values = new HashMap<>();

    public String getSessionId() {
        return values.get("JSESSIONID");
    }

    public boolean hasSession() {
        return values.containsKey("JSESSIONID");
    }

    public boolean hasNoSession() {
        return !hasSession();
    }

    public void add(final String name, final String value) {
        values.put(name, value);
    }
}
