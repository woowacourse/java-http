package org.apache.coyote.http.request;

import java.util.Arrays;

public enum HttpMethod {

    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE"),
    ;

    public final String value;

    HttpMethod(String value) {
        this.value = value;
    }

    public static HttpMethod from(String value) {
        return Arrays.stream(values())
                     .filter(httpMethod -> httpMethod.value.equals(value.toUpperCase()))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("메서드 이름이 유효하지 않습니다. 입력: " + value));
    }
}
