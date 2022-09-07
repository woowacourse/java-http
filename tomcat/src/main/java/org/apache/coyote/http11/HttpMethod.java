package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    PATCH,
    DELETE;

    public static HttpMethod find(String value) {
        return Arrays.stream(values())
                .filter(httpMethod1 -> httpMethod1.name().equals(value))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("알맞은 http method가 없습니다."));
    }
}
