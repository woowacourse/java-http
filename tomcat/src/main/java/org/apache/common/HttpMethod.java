package org.apache.common;

import java.util.Arrays;
import java.util.Objects;

public enum HttpMethod {

    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE");

    private final String name;

    HttpMethod(String name) {
        this.name = name;
    }

    public static HttpMethod of(String value) {
        return Arrays.stream(values())
                .filter(httpMethod -> Objects.equals(httpMethod.name, value))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("허용되지 않는 HttpMethod 입니다."));
    }

    public String getName() {
        return name;
    }
}
