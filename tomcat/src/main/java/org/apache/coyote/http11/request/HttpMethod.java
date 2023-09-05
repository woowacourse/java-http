package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum HttpMethod {

    GET,
    POST
    ;

    public static HttpMethod valueOfHttpStatus(final String httpMethod) {
        return Arrays.stream(values())
                .filter(method -> method.name().equals(httpMethod))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 HttpMethod 입니다."));
    }

}
