package org.apache.coyote.http11.common;

import java.util.Arrays;

public enum HttpMethod {
    GET,
    POST;

    public static HttpMethod from(final String input) {
        return Arrays.stream(values())
                .filter(value -> value.name().equals(input))
                .findAny()
                .orElseThrow(() -> new Http11Exception("올바르지 않은 HttpMethod 형식입니다."));
    }
}
