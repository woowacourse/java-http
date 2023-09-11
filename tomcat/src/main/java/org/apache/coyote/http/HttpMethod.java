package org.apache.coyote.http;

import java.util.Arrays;

public enum HttpMethod {
    GET("GET"),
    POST("POST");

    private final String name;

    HttpMethod(String name) {
        this.name = name;
    }

    public static HttpMethod from(String method) {
        return Arrays.stream(HttpMethod.values())
            .filter(httpMethod -> httpMethod.getName().equals(method))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 HTTP Method 입니다."));
    }

    public String getName() {
        return name;
    }
}
