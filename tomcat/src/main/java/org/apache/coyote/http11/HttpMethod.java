package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    PATCH;

    public static HttpMethod from(String method) {
        return Arrays.stream(HttpMethod.values())
                .filter(value -> value.name().equalsIgnoreCase(method))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid HTTP method: " + method));
    }
}
