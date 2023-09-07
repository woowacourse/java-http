package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {

    GET("GET"), POST("POST"), PUT("PUT"), PATCH("PATCH");

    private final String methodName;

    HttpMethod(String methodName) {
        this.methodName = methodName;
    }

    public static HttpMethod findByValue(String value) {
        return Arrays.stream(HttpMethod.values())
                .filter(httpMethod -> httpMethod.methodName.equals(value))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Not Supported Http Version"));
    }
}
