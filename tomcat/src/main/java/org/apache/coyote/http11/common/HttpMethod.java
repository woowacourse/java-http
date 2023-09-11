package org.apache.coyote.http11.common;

import java.util.Arrays;

public enum HttpMethod {
    OPTIONS("OPTIONS"),
    GET("GET"),
    HEAD("HEAD"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    TRACE("TRACE"),
    CONNECT("CONNECT"),
    ;

    private final String method;

    HttpMethod(final String method) {
        this.method = method;
    }

    public static HttpMethod from(final String method) {
        return Arrays.stream(values())
                .filter(value -> value.method.equals(method))
                .findFirst()
                .orElseThrow();
    }
}
