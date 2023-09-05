package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private final Map<String, String> values;

    public static HttpCookie ofJSessionId(final String jSessionId) {
        final Map<String, String> values = new HashMap<>();
        values.put("JSESSIONID", jSessionId);

        return new HttpCookie(values);
    }

    public HttpCookie(final Map<String, String> values) {
        this.values = values;
    }

    public String getValues() {
        return values.keySet()
                .stream()
                .map(key -> key + "=" + values.get(key) + " ")
                .collect(Collectors.joining());
    }
}
