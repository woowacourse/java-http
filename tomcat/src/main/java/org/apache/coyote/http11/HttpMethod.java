package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {

    GET("GET"),
    POST("POST"),
    PATCH("PATCH"),
    PUT("PUT"),
    DELETE("DELETE");

    private final String value;

    HttpMethod(String value) {
        this.value = value;
    }

    public static HttpMethod from(String value) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 HTTP 메소드 요청입니다."));
    }
}
