package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {

    GET,
    POST,
    ;

    public static HttpMethod findByName(String method) {
        return Arrays.stream(values())
                .filter(httpMethod -> method.equals(httpMethod.name()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 메소드입니다."));
    }
}
