package org.apache.coyote.http11.message;

import java.util.Arrays;

public enum HttpMethod {
    GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS, TRACE, CONNECT;

    public static HttpMethod getByValue(final String value) {
        return Arrays.stream(HttpMethod.values())
                .filter(item -> item.name().equals(value.toUpperCase()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 HTTP Method 값 입니다. - " + value));
    }
}
