package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum HttpMethod {

    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE"),
    OPTIONS("OPTIONS");

    private final String method;

    HttpMethod(String method) {
        this.method = method;
    }

    public static HttpMethod from(String httpMethod) {
        return Arrays.stream(values())
                .filter(value -> value.method.equals(httpMethod))
                .findFirst()
                .orElseThrow(IllegalAccessError::new);
    }

    public String getMethod() {
        return method;
    }
}
