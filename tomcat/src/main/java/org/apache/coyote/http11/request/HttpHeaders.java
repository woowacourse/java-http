package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class HttpHeaders {

    public static final String COOKIE = "cookie";
    public static final String CONTENT_LENGTH = "content-length";

    private final Map<String, String> values;

    public HttpHeaders(Map<String, String> values) {
        this.values = values;
    }

    public Optional<String> find(String name) {
        return Optional.ofNullable(values.get(name.toLowerCase(Locale.ROOT)));
    }

    public static HttpHeaders empty() {
        return new HttpHeaders(new HashMap<>());
    }
}
