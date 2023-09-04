package org.apache.coyote.http;

import java.util.Arrays;

public enum HttpMethod {
    GET,
    POST;

    public static HttpMethod from(final String target) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.name().equals(target.toUpperCase()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘 못된 HTTP 요청입니다."));
    }
}
