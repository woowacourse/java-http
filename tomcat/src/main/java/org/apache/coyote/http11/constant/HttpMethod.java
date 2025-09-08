package org.apache.coyote.http11.constant;

import java.util.Arrays;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    FETCH;

    public static HttpMethod from(String methodName) {
        return Arrays.stream(values())
                .filter(method -> method.name().equals(methodName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 HTTP 메서드 타입입니다."));
    }
}
