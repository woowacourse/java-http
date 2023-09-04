package org.apache.coyote.http11.request.start;

import java.util.Arrays;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    PATCH;

    public static HttpMethod from(final String httpMethod) {
        return Arrays.stream(HttpMethod.values())
                .filter(method -> method.name().equalsIgnoreCase(httpMethod))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 http요청입니다."));
    }
}
