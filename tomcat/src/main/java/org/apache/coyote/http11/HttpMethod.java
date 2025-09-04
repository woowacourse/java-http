package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {
    GET, POST, PUT, PATCH, DELETE;

    public static HttpMethod from(String name) {
        return Arrays.stream(values())
            .filter(method -> method.name().equalsIgnoreCase(name))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("잘못된 HTTP 메서드입니다."));
    }
}
