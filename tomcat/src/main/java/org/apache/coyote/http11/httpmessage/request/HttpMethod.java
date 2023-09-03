package org.apache.coyote.http11.httpmessage.request;

import java.util.Arrays;

public enum HttpMethod {

    GET,
    POST,
    PUT,
    DELETE;

    public static HttpMethod getMethod(final String requestMethod) {
        return Arrays.stream(HttpMethod.values())
            .filter(value -> value.name().equals(requestMethod))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 httpMethod 입니다."));
    }
}
