package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class HttpCookie {

    private final LinkedHashMap<String, String> values;

    public HttpCookie() {
        this(new LinkedHashMap<>());
    }

    private HttpCookie(final LinkedHashMap<String, String> values) {
        this.values = values;
    }

    public static HttpCookie of(final String input) {
        LinkedHashMap<String, String> cookies = new LinkedHashMap<>();
        for (String cookie : input.split("; ")) {
            String[] splitCookie = cookie.split("=", 2);
            cookies.put(splitCookie[0], splitCookie[1]);
        }
        return new HttpCookie(cookies);
    }

    public HttpCookie add(final String key, final String value) {
        values.put(key, value);
        return this;
    }

    public String joinToString() {
        return values.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("; "));
    }
}
