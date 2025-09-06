package org.apache.coyote.httpHeader;

import java.util.Arrays;

public enum HttpMethod {

    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE"),
    HEAD("HEAD"),
    OPTIONS("OPTIONS");

    private final String method;

    HttpMethod(final String method) {
        this.method = method;
    }

    public static HttpMethod findHttpMethod(final String input) {
        return Arrays.stream(HttpMethod.values())
                .filter(method -> method.method.equals(input))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 HTTPMethod 입니다."));
    }
}
