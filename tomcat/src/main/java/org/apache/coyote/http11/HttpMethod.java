package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {

    GET, POST, PUT, DELETE, HEAD, OPTIONS, PATCH, CONNECT;

    public static HttpMethod from(final String method) {
        return Arrays.stream(HttpMethod.values())
                .filter(httpMethod -> httpMethod.name().equals(method.toUpperCase()))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("HTTP Method not supported: " + method));
    }
}
