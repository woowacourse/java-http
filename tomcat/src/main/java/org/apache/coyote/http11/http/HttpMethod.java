package org.apache.coyote.http11.http;

import java.util.Arrays;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    PATCH,
    DELETE,
    OPTIONS,
    HEAD,
    ;

    public static HttpMethod from(String value) {
        return Arrays.stream(values())
                .filter(m -> m.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 HTTP 메서드: " + value));
    }
}
