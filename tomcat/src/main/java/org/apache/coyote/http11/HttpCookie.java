package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String COOKIE_DELIMITER = "; ";
    private static final String COOKIE_SEPARATOR = "=";

    private final Map<String, String> values;

    public static HttpCookie ofJSessionId(final String jSessionId) {
        final Map<String, String> values = new HashMap<>();
        values.put("JSESSIONID", jSessionId);

        return new HttpCookie(values);
    }

    public HttpCookie(final Map<String, String> values) {
        this.values = values;
    }

    public String printValues() {
        return values.keySet()
                .stream()
                .map(key -> key + COOKIE_SEPARATOR + values.get(key) + COOKIE_DELIMITER)
                .collect(Collectors.joining());
    }

    public boolean isNotEmpty() {
        return !values.isEmpty();
    }
}
