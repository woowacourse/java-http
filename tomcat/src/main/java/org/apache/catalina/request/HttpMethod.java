package org.apache.catalina.request;

import java.util.Arrays;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    ;

    private final String value;

    HttpMethod(String value) {
        this.value = value;
    }

    public static HttpMethod of(String value) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("지원하지 않는 요청 방식입니다: " + value));
    }
}
