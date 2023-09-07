package org.apache.common;

import java.util.Arrays;
import java.util.Objects;

public enum HttpMethod {
    GET("GET"), POST("POST");

    private final String methodName;

    HttpMethod(String methodName) {
        this.methodName = methodName;
    }

    public static HttpMethod from(String methodName) {
        return Arrays.stream(values())
                .filter(httpMethod -> Objects.equals(httpMethod.methodName, methodName))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 HTTP 메소드입니다"));
    }
}
