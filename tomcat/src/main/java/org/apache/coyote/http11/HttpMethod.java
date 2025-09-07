package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    PATCH,
    DELETE,
    HEAD,
    TRACE,
    OPTIONS,
    CONNECT;

    public static HttpMethod getMethod(String method) {
        return Arrays.stream(HttpMethod.values())
                .filter(httpMethod -> httpMethod.name().equalsIgnoreCase(method))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
