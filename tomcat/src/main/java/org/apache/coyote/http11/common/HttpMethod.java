package org.apache.coyote.http11.common;

import java.util.Arrays;

public enum HttpMethod {

    GET,
    POST,
    ;

    public static HttpMethod from(String method) {
        String message = "지원하지 않는 메서드입니다. method: %s".formatted(method);

        return Arrays.stream(values())
                .filter(value -> value.name().equals(method))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(message));
    }
}
