package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {

    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE"),
    HEAD("HEAD"),
    TRACE("TRACE"),
    OPTIONS("OPTIONS");

    private final String value;

    HttpMethod(String value) {
        this.value = value;
    }

    public static HttpMethod from(String method) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.value.equals(method))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 http method입니다."));
    }

    public String getValue() {
        return value;
    }
}
