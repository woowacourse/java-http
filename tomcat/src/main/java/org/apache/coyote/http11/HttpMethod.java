package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {
    POST("post"),
    GET("get");

    private final String value;

    HttpMethod(final String value) {
        this.value = value;
    }

    public static HttpMethod from(final String httpMethod) {
        return Arrays.stream(values())
                .filter(it -> it.getValue().equalsIgnoreCase(httpMethod))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("문자열과 매칭되는 HttpMethod 가 존재하지 않습니다."));
    }

    public String getValue() {
        return value;
    }
}
