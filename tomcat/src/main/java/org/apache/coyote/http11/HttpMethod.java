package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {
    GET("GET"),
    POST("POST");

    private final String method;

    HttpMethod(String method) {
        this.method = method;
    }

    public static HttpMethod valueOfMethod(String method) {
        return Arrays.stream(values())
                .filter(value -> value.method.equals(method))
                .findAny()
                .orElseThrow();
    }
}
