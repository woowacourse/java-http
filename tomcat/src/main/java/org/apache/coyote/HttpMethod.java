package org.apache.coyote;

import java.util.Arrays;

public enum HttpMethod {

    GET,
    POST,
    PUT,
    PATCH,
    DELETE;

    public static HttpMethod from(String method) {
        return Arrays.stream(HttpMethod.values())
                .filter(httpMethod -> httpMethod.name().equalsIgnoreCase(method))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("유효하지 않은 HTTP 메서드입니다."));
    }
}
