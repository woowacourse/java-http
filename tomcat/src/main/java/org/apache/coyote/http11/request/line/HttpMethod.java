package org.apache.coyote.http11.request.line;

import java.util.Arrays;

public enum HttpMethod {

    GET,
    POST,
    PUT,
    PATCH,
    DELETE,
    HEAD,
    CONNECT,
    OPTIONS;

    public static HttpMethod from(String method) {
        return Arrays.stream(HttpMethod.values())
                .filter(httpMethod -> httpMethod.name().equalsIgnoreCase(method))
                .findAny().orElseThrow(() -> new IllegalArgumentException("해당하는 HTTP 메서드를 찾을 수 없습니다."));
    }

    public boolean isGet() {
        return this == GET;
    }

}
