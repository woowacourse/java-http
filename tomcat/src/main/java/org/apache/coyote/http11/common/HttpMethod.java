package org.apache.coyote.http11.common;

import java.util.Arrays;

public enum HttpMethod {
    GET,
    POST,
    ;

    public static HttpMethod from(final String method) {
        return Arrays.stream(HttpMethod.values())
                .filter(httpMethod -> httpMethod.name().equals(method.toUpperCase()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 HTTP 메서드입니다."));
    }
}
