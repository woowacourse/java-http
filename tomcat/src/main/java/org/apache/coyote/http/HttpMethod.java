package org.apache.coyote.http;

import java.util.Arrays;

public enum HttpMethod {
    GET("get"),
    POST("post"),
    PUT("put"),
    PATCH("patch"),
    DELETE("delete"),
    OPTIONS("options"),
    ;

    private final String httpMethod;

    HttpMethod(final String method) {
        this.httpMethod = method;
    }

    public static HttpMethod from(String httpMethod) {
        return Arrays.stream(values())
                .filter(it -> it.compare(httpMethod))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));
    }

    private boolean compare(final String method) {
        return this.httpMethod.equalsIgnoreCase(method);
    }
}
