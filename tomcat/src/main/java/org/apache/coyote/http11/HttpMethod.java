package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {

    GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS;

    public static HttpMethod from(String method) {
        return Arrays.stream(values())
                .filter(value -> value.name().equals(method))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 HTTP 메서드입니다: " + method));
    }
}
