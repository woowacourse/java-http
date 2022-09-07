package org.apache.coyote.domain.request.requestline;

import java.util.Arrays;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE;

    public static HttpMethod get(String method) {
        return Arrays.stream(HttpMethod.values())
                .filter(value -> value.name().equals(method))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메서드입니다."));
    }
}
