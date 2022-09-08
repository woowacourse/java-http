package org.apache.coyote.request;

import java.util.Arrays;

public enum HttpMethod {

    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE");

    private final String httpMethod;

    HttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public static HttpMethod from(String httpMethod) {
        return Arrays.stream(values())
                .filter(it -> it.httpMethod.equals(httpMethod))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 HTTP 메소드입니다."));
    }

    @Override
    public String toString() {
        return this.httpMethod;
    }
}
