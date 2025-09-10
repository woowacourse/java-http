package org.apache.coyote.http11.message;

import java.util.Arrays;

public enum HttpMethod {
    GET,
    POST,
    ;

    public static HttpMethod findByName(String name) {
        return Arrays.stream(values())
                .filter(value -> value.name().equals(name))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("일치하는 HTTP Method 가 존재하지 않습니다."));
    }
}
