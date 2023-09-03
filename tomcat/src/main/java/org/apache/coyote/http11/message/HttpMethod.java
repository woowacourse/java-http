package org.apache.coyote.http11.message;

import java.util.Arrays;

public enum HttpMethod {

    GET;

    public static HttpMethod from(final String requestMethod) {
        return Arrays.stream(values())
            .filter(method -> method.name().equals(requestMethod))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("지원되지 않는 HTTP 메소드입니다."));
    }
}
