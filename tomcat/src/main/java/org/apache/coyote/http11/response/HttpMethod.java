package org.apache.coyote.http11.response;

import java.util.Arrays;

public enum HttpMethod {

    GET, POST;

    public static HttpMethod of(final String value) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.name().equalsIgnoreCase(value))
                .findAny()
                .orElseThrow(() -> new RuntimeException("지원하지 않는 http 메서드입니다."));
    }
}
