package org.apache.coyote.http11.common;

import org.apache.coyote.http11.exception.NotMatchedHttpMethodException;

import java.util.Arrays;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    HEAD,
    OPTIONS,
    TRACE,
    CONNECT,
    PATCH;

    public static HttpMethod of(String method) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.name().equalsIgnoreCase(method))
                .findFirst()
                .orElseThrow(NotMatchedHttpMethodException::new);
    }
}
