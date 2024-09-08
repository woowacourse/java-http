package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {
    POST("POST"),
    GET("GET"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE"),
    OPTIONS("OPTIONS"),
    TRACE("TRACE"),
    HEAD("HEAD"),
    CONNECT("CONNECT"),
    ;

    private final String value;

    HttpMethod(String value) {
        this.value = value;
    }

    public static HttpMethod findByValue(String value) {
        return Arrays.stream(values())
                .filter(method -> method.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 HttpMethod 값입니다. (%s) ".formatted(value)));
    }

    public String getValue() {
        return value;
    }
}
