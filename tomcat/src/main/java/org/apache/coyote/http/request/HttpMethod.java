package org.apache.coyote.http.request;

import java.util.Arrays;

public enum HttpMethod {

    GET,
    POST,
    PUT,
    PATCH,
    DELETE;

    public static HttpMethod from(final String source) {
        return Arrays.stream(values())
                .filter(method -> method.name().equals(source))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 HTTP 메서드입니다: " + source));
    }
}
