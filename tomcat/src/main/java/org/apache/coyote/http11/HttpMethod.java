package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {
    GET("GET"), POST("POST");

    private final String value;

    HttpMethod(String value) {
        this.value = value;
    }

    public static HttpMethod of(String method) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.value.equals(method))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "지원하지 않는 HTTP 메서드입니다: " + method
                        )
                );
    }
}
