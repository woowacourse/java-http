package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {
    POST("POST"),
    GET("GET");

    private final String value;

    HttpMethod(String value) {
        this.value = value;
    }

    public static HttpMethod of(String value) {
        return Arrays.stream(values())
                .filter(method -> method.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid method: " + value));
    }

    public boolean isPost() {
        return this == POST;
    }

    public boolean isGet() {
        return this == GET;
    }
}
