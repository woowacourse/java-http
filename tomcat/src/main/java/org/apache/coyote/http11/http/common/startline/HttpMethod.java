package org.apache.coyote.http11.http.common.startline;

import java.util.Arrays;

public enum HttpMethod {

    GET,
    POST,
    PUT,
    DELETE,
    ;

    public static HttpMethod findMethod(String target) {
        return Arrays.stream(HttpMethod.values())
                .filter(httpMethod -> httpMethod.name().equals(target))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("지원되지 않는 http method입니다 %s".formatted(target)));
    }
}
