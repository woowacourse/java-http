package org.apache.coyote;

import java.util.Arrays;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    PATCH,
    ;

    public static HttpMethod from(String method) {
        return Arrays.stream(HttpMethod.values())
                .filter(value -> value.name().equalsIgnoreCase(method))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("허용되지 않는 HTTP 메서드입니다. method = " + method));
    }
}
