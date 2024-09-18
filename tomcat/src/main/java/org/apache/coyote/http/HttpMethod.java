package org.apache.coyote.http;

import java.util.Map;

public enum HttpMethod implements HttpComponent {
    GET,
    POST,
    ;

    private static final Map<String, HttpMethod> CACHE = Map.of(
            "GET", GET,
            "POST", POST
    );

    public static HttpMethod from(final String value) {
        return CACHE.get(value);
    }

    public boolean isGet() {
        return this == GET;
    }

    public boolean isPost() {
        return this == POST;
    }

    @Override
    public String asString() {
        return name();
    }
}
