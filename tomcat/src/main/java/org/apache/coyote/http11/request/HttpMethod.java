package org.apache.coyote.http11.request;

import java.util.Map;
import java.util.Optional;

public enum HttpMethod {
    GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS, TRACE, CONNECT;

    private static final Map<String, HttpMethod> methods = Map.of(
            "GET", GET,
            "POST", POST,
            "PUT", PUT,
            "PATCH", PATCH,
            "DELETE", DELETE,
            "HEAD", HEAD,
            "OPTIONS", OPTIONS,
            "TRACE", TRACE,
            "CONNECT", CONNECT
    );

    public static HttpMethod from(String name) {
        return Optional.ofNullable(methods.get(name))
                .orElseThrow(() -> new IllegalArgumentException("Not existing Http method."));
    }
}
