package org.apache.coyote.common;

import java.util.Arrays;

public enum HttpMethod {
    GET,
    HEAD,
    POST,
    PUT,
    PATCH,
    DELETE,
    OPTIONS,
    TRACE,
    ;

    public static HttpMethod from(String value) {
        return Arrays.stream(HttpMethod.values())
                .filter(httpMethod -> httpMethod.name().equals(value.toUpperCase()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
