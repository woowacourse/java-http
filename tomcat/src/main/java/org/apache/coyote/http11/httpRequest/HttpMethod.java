package org.apache.coyote.http11.httpRequest;

import java.util.Arrays;

public enum HttpMethod {

    GET,
    POST,
    ;

    public static HttpMethod from(String method) {
        return Arrays.stream(values())
            .filter(value -> value.name().equalsIgnoreCase(method))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 요청 방식입니다."));
    }
}
