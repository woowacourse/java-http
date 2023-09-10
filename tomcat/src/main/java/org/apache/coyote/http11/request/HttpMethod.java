package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum HttpMethod {
    GET("GET"),
    POST("POST");

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
