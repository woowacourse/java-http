package org.apache.coyote.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> values;

    private HttpCookie(final Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie parse(final String lines) {
        final Map<String, String> values = new HashMap<>();
        final List<String> cookies = List.of(lines.split("; "));

        for (final String cookie : cookies) {
            final List<String> value = List.of(cookie.split("="));
            values.put(value.get(0), value.get(1));
        }
        return new HttpCookie(values);
    }

    public String getSessionId() {
        return values.get("JSESSIONID");
    }
}
