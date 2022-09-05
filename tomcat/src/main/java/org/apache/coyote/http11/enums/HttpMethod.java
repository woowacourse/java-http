package org.apache.coyote.http11.enums;

import java.util.Arrays;

public enum HttpMethod {

    POST("post"),
    GET("get"),
    ;

    private final String value;

    HttpMethod(final String value) {
        this.value = value;
    }

    public static HttpMethod of(final String value) {
        return Arrays.stream(HttpMethod.values())
                .filter(it -> value.equalsIgnoreCase(it.value))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("지원하지 않는 형식의 HttpMethod입니다. -> value: %s", value)
                ));
    }
}
