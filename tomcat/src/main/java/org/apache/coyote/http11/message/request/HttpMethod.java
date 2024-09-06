package org.apache.coyote.http11.message.request;

import java.util.Arrays;

public enum HttpMethod {
    GET("GET"),
    HEAD("HEAD"),
    PUT("PUT"),
    PATCH("PATCH"),
    POST("POST"),
    DELETE("DELETE"),
    TRACE("TRACE"),
    OPTIONS("OPTIONS"),
    CONNECT("CONNECT");

    private final String method;

    HttpMethod(String method) {
        this.method = method;
    }

    public static HttpMethod from(String method) {
        return Arrays.stream(values())
                .filter(httpMethod -> method.equals(httpMethod.method))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 HTTP 메소드입니다."));
    }
}
