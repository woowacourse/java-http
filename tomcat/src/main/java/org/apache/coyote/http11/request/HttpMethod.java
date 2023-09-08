package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum HttpMethod {
    GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS, TRACE, CONNECT;

    public static HttpMethod from(String name) {
        return Arrays.stream(values()).filter(method -> method.name().equalsIgnoreCase(name))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Not existing Http method."));
    }
}
