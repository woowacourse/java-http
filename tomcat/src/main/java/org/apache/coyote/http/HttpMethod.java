package org.apache.coyote.http;

import java.util.Arrays;

public enum HttpMethod {
    GET,
    POST;

    public static HttpMethod findByName(final String name) {
        return Arrays.stream(values())
                .filter(method -> method.name().equals(name))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 HTTP 메소드입니다."));
    }
}
